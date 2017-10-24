#Paxos协议是什么?
###先给出Paxos算法的设计目的，和算法流程，再反过来分析算法的原理。
---
Paxos算法实现的是分布式系统多个结点之上数据的一致性，这个算法有如下特性
* 1.基于消息传递，允许消息传输的丢失，重复，乱序，但是不允许消息被攥改
* 2.在结点数少于半数失效的情况下仍然能正常的工作，结点失效可以在任何时候发生而不影响算法正常执行。

---
下面是Basic Paxos算法，注意，这个算法只具有在多个冲突请求中选出一个的功能，并不具备序列化多个请求依次执行的功能。
具体算法流程如下，为避免歧义，关键字眼Propose,Proposal,Accept,Value,Choose等保留英文原文。

阶段1a---Prepare（预定Proposal序号）

每个Proposor 拿到某个Client的请求Value_i后，在此阶段还不能发起Proposal，只能发送一个Proposal序号N，将序号发送给所有Acceptor（即所有Server包括自己），整个系统中所有Proposal的序号不能重复而且每个Proposor自己用到的序号必须是递增的，通常的做法是，假设K台Server协同运行Paxos算法，那么Server_i（i=0...K-1)用的Proposal序号初始值为i，以后每次要产生新序号时递增K，这样保证了所有Server的Proposal序号不重复。
阶段1b---Respond with Promise
每个Acceptor收到Proposal序号后，先检查之前是否Repond序号更高的Proposal，若没有，那么就给出Response，这个Response带有自己已经Accept的序号最高的Proposal（若还没Accept任何Proposal，回复null），同时，Promise自己不再Accept低于接收序号的Proposal。否则，拒绝Respond。

阶段2a---发起Proposal，请求Accept
Proposal如果得到了来自超过半数的Acceptor的Response，那么就有资格向Acceptor发起Proposal<N,value>。其中，N是阶段1a中发送的序号，value是收到的Response中序号最大的Proposal的Value，若收到的Response全部是null，那么Value自定义，可直接选一个Client请求的Value_i

阶段2b--Accept Proposal
检查收到的Proposal的序号是否违反阶段1b的Promise，若不违反，则Accept收到的Proposal。

所有Acceptor Accept的Proposal要不断通知所有Learner，或者Learner主动去查询，一旦Learner确认Proposal已经被超过半数的Acceptor Accept，那么表示这个Proposal 的Value 被 Chosen，Learner就可以学习这个Proposal的Value，同时在自己Server上就可以不再受理Proposor的请求。

这个算法能达到什么效果呢，只要保证超过半数的Server维持正常工作，同时连接工作Server的网络正常（网络允许消息丢失，重复，乱序），就一定能保证，

---
P2a: 在将来某一时刻，自从某个Proposal被多数派Acceptor Accept后，之后Accept的Proposal Value一定和这个Proposal Value相同。

这就是整个算法的关键，保证了这一点，剩下的Learn Value过程就简单了，无需再为消息丢失，Server宕机而担心，例如，假设5台Server编号0~4，Server0，Server1，Server2已经Accept Proposal 100，然后Server0,Server1学习到Proposal 100，刚学习完成Server0,Server1就都宕机了，但这时候，Server2 Server3和Server4由于没有学习到Chosen value，因此还要继续提出Proposal，然后呢，根据这个神奇的算法，最后能使得Server3 Server4将来Accept的值一定是之前选出来过的Proposal 100的Value。

看到这里，大家应该能够隐隐猜到，在这个过程中，Server2之前Accept Proposal 100的Value起了关键作用，下面，我们就来严格证明上述红色字体表示的算法关键点：

首先回顾前边两阶段协议的几个关键点：
1.发起Proposal前要先获得多数派Acceptor中Accept过的序号最大的Proposal Value。若Value为null才能采用自己的Value。
2.阶段1b Promise自己不再Accept低于接收序号的Proposal。
3.Propsal被超半数的Acceptor Accept才能被认定为Chosen Value从而被Learner学习。

这几个约束条件共同作用，达到了上述P2a要求的效果，Paxos算法提出者Leslie Lamport是怎么构造出来的呢，事实上很简单：
首先，把P2a加强为如下条件：

P2b:自从某个Proposal被多数派Acceptor Accept后，之后Proposor提出的Proposal Value一定和这个Proposal Value相同。

显而易见，由P2b可以推出P2a，那么怎么满足P2b呢，实际上，只要满足如下条件：

P2c:发起的Proposal的Value为任意一个多数派Acceptor集合中Accept过的序号最大的Proposal Value。若这个Acceptor集合中没有Accept过Proposal才能采用自己的Value。

如何从P2c推出P2b呢，利用数学归纳法可以轻易做出证明：假设在某一时刻一个超半数Acceptor集合C共同Accept了某个Proposal K，由于集合C和任意一个多数派Acceptor集合S必有一个共同成员，那么，在这个时刻之后，任意一个多数派Acceptor集合S 中Accept过的最大序号的Proposal只可能是Proposal K或序号比Proposal K更大的Proposal，假设为Proposal K2。同理，Proposal K2的Value等于Proposal K或Proposal K3的Value，而K<K3<K2，递推下去，最终推出根据P2c定出的Value必然是Proposal K的Value。

我们可以看到，P2c条件基本就是上述两阶段协议的关键点1，但是还有一个问题，这个P2c条件要求找出这个“最大序号Value”和提出Proposal必须是一个原子操作，这实际上是难以实现的，所以，上述两阶段协议用了一个巧妙的方法避开了这个问题，这就是上述关键点2 Promise所起的作用了。在Acceptor respond“最大序号Value”的时候，Promise不再Accept低于收到序号的Proposal，这样“找出这个‘最大序号Value’”和“提出Proposal”之间就不可能插入新的被Accept的序号，从而避免P2c条件被破坏。

到这里为止，基本的Paxos算法就已经透彻分析完了，但是，现在这个算法是使用多个Proposal，会造成活锁问题，需要引入leader来优化，而且，这个算法还只能实现在多个冲突Value中选举一个Value的功能，至于序列化多个Value实现状态机，就需要multi-paxos算法。这些问题，
[请点击这篇文章](http://blog.csdn.net/anderscloud/article/details/7246928)



