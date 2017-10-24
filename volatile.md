Java并发编程
在当前的Java内存模型下，线程可以把变量保存在本地内存（比如机器的寄存器）中，而不是直接在主存中进行读写。这就可能造成一个线程在主存中修改了一个变量的值，
而另外一个线程还继续使用它在寄存器中的变量值的拷贝，造成数据的不一致。 

要解决这个问题，只需要像在本程序中的这样，把该变量声明为volatile（不稳定的）即可，这就指示JVM，这个变量是不稳定的，每次使用它都到主存中进行读取。一般说来，多任务环境下各任务间共享的标志都应该加volatile修饰。 


Volatile修饰的成员变量在每次被线程访问时，都强迫从共享内存中重读该成员变量的值。而且，当成员变量发生变化时，强迫线程将变化值回写到共享内存。这样在任何时刻，两个不同的线程总是看到某个成员变量的同一个值。 

用volatile和不用volatile的区别，运行一下，就知道了。

不用volatile：
package com.keyword;  
  
public class TestWithoutVolatile {  
    private static boolean bChanged;  
  
    public static void main(String[] args) throws InterruptedException {  
        new Thread() {  
  
            @Override  
            public void run() {  
                for (;;) {  
                    if (bChanged == !bChanged) {  
                        System.out.println("!=");  
                        System.exit(0);  
                    }  
                }  
            }  
        }.start();  
        Thread.sleep(1);  
        new Thread() {  
  
            @Override  
            public void run() {  
                for (;;) {  
                    bChanged = !bChanged;  
                }  
            }  
        }.start();  
    }  
  
}  

运行后，程序进入死循环了，一直在运行。


用volatile：

package com.keyword;  
  
public class TestWithVolatile {  
    private static volatile boolean bChanged;  
  
    public static void main(String[] args) throws InterruptedException {  
        new Thread() {  
  
            @Override  
            public void run() {  
                for (;;) {  
                    if (bChanged == !bChanged) {  
                        System.out.println("!=");  
                        System.exit(0);  
                    }  
                }  
            }  
        }.start();  
        Thread.sleep(1);  
        new Thread() {  
  
            @Override  
            public void run() {  
                for (;;) {  
                    bChanged = !bChanged;  
                }  
            }  
        }.start();  
    }  
  
}  

程序输出!=，然后马上退出。
但是，很多情况下，用不用volatile，感觉不出什么区别，什么时候要用volatile呢？看看JDK里使用volatile的类。
比如java.util.regex.Pattern里的变量：

程序输出!=，然后马上退出。
但是，很多情况下，用不用volatile，感觉不出什么区别，什么时候要用volatile呢？看看JDK里使用volatile的类。
比如java.util.regex.Pattern里的变量：

private transient volatile boolean compiled = false;  
还有，java.lang.System的变量：
private static volatile Console cons = null;  

一般就是初始化的时候，需要用到volatile。
java.util.Scanner里的变量，如：
private static volatile Pattern boolPattern;  
private static volatile Pattern separatorPattern;  
private static volatile Pattern linePattern;  

初始化boolPattern的代码：

private static Pattern boolPattern() {  
        Pattern bp = boolPattern;  
        if (bp == null)  
            boolPattern = bp = Pattern.compile(BOOLEAN_PATTERN,  
                                          Pattern.CASE_INSENSITIVE);  
        return bp;  
}  

上面的情况，可以使用synchronized来对boolPattern加锁，但是synchronized开销比volatile大，volatile能够胜任上面的工作。

volatile不保证原子操作，所以，很容易读到脏数据。

使用建议：在两个或者更多的线程访问的成员变量上使用volatile。当要访问的变量已在synchronized代码块中，或者为常量时，不必使用。 

参考：
http://sudalyl.blog.163.com/blog/static/1018092742010925901769/
###下面的链接地址
*[参考地址](http://sudalyl.blog.163.com/blog/static/1018092742010925901769/)