package arthas;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Arthas 是 Alibaba开源的 Java诊断工具：https://alibaba.github.io/arthas/quick-start.html。
 * 可以方便的定位和诊断线上程序运行问题，而不必挨个执行 JDK 命令去分析。是一款命令行交互模式的 Java 诊断工具，由于是 Java 编写，
 * 所以可以直接下载相应 的 jar 包运行。
 *
 * github下载，wget https://alibaba.github.io/arthas/arthas-boot.jar。
 * Gitee 下载，wget https://arthas.gitee.io/arthas-boot.jar。
 *
 * 运行 jar 包，并且运行时或者运行之后选择要监测的 Java 进程，执行 java -jar arthas-boot.jar，然后选择对应进程的编号回车即可。
 * 也可以打印出帮助信息，java -jar arthas-boot.jar -h PID。
 * 在出现 Arthas Logo 之后就可以使用命令进行问题诊断了。
 *
 * 执行该程序的用户需要和目标进程具有相同的权限。比如以admin用户来执行：sudo su admin && java -jar arthas-boot.jar 或 sudo -u
 * admin -EH java -jar arthas-boot.jar。
 * 如果attach不上目标进程，可以查看~/logs/arthas/ 目录下的日志。
 * 使用 shutdown 退出时，Arthas 会自动重置所有增强过的类 。
 *
 * 查看运行的 java 进程信息，可使用 ps 或 jps 两种方式：jps -mlvV。筛选 java 进程信息：jps -mlvV | grep xxx。
 *
 * Arthas 目前支持 Web Console，在成功启动连接进程之后就已经自动启动，可以直接访问 http://127.0.0.1:8563/ 访问，页面上的操作
 * 模式和控制台完全一样。常用命令有：
 * dashboard，	当前系统的实时数据面板，展示当前进程的信息。可以概览程序的 线程、内存、GC、运行环境信息。
 * thread，		查看当前 JVM 所有线程的堆栈信息，及每个线程的 CPU 使用率。thread 1 | grep 'main(' 即打印线程ID 1的栈，通常是main函数的线程。
 * watch，		查看输入输出参数以及异常等信息。
 * trace，		可以跟踪统计方法耗时，并输出方法在每个节点上的耗时。
 * stack，		输出当前方法被调用的调用路径。stack classFullName methodName。
 * tt，			记录方法执行的详细情况，记录指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测。
 * monitor，		监控统计方法的执行情况。每5秒统计一次 CheckTimeout 类的 get 方法执行情况（monitor -c 5 arthas.CheckTimeout get）。
 * jvm，			查看当前 JVM 信息。
 * vmoption，	查看，更新 JVM 诊断相关的参数。
 * sc，			查看 JVM 已加载的类信息。sc -d -f  classFullName，查看类的字段信息。
 * sm，			查看已加载类的方法信息。sm classFullName。
 * jad，			反编译指定已加载类（全类名）的源码。
 * classloader，	查看 classloader 的继承树，urls，类加载信息。
 * heapdump，	类似 jmap 命令的 heap dump 功能。
 * ognl，		ognl '@classFullName@变量名'，查看某个变量中的数据。ognl '@com.Arthas@hashSet.size()'，ognl  '@com.Arthas@hashSet.add("test")'。
 *
 *
 * 如果是在控制台中，则直接输入对应命令回车即可查看相关的信息。
 *
 * thread 命令，在使用时先通过观察总体的线程信息，然后查看具体的线程运行情况。如果只是为了寻找 CPU 使用较高的线程，可以直接使用
 * 命令 thread -n [显示的线程个数] ，就可以排列出 CPU 使用率 Top N 的线程。
 * thread 线程ID 命令，可以看到指定线程的信息，并且会输出 CPU 使用较高的方法和行数。
 * thread -b（找出当前阻塞其他线程的线程，死锁，直接定位到死锁信息），但目前只支持 synchronized 关键字阻塞住的线程，不支持 lock。
 *
 * jad 命令还提供了一些其他参数：
 * 反编译只显示源码，jad --source-only classFullName。反编译某个类的某个方法，jad --source-only classFullName methodName。
 *
 * 先回顾一下线程的几种常见状态：
 * 1，RUNNABLE 运行中。
 * 2，TIMED_WAITIN 进入该状态调用的方法：Thread.sleep()，Object.wait() 并加了超时参数，Thread.join() 并加了超时参数。LockSupport.parkNanos()，LockSupport.parkUntil()。
 * 3，WAITING 进入该状态调用的方法：Object.wait() 而且不加超时参数，Thread.join() 而且不加超时参数，LockSupport.park()。
 * 4，BLOCKED 阻塞，等待锁。
 * 使用 thread | grep pool 命令查看线程池里线程信息。
 */
public class ArthasDemo {

    private static HashSet hashSet = new HashSet();
    /** 线程池，大小1*/
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        // 模拟 CPU 过高，这里注释掉了，测试时可以打开
        // cpu();
        // 模拟线程阻塞
        thread();
        // 模拟线程死锁
        deadThread();
        // 不断的向 hashSet 集合增加数据
        addHashSetThread();
    }

    /**
     * 不断的向 hashSet 集合添加数据。
     *
     * 使用 watch 命令：
     * 查看入参和出参，watch arthas.ArthasDemo addHashSetThread '{params[0],returnObj}'
     * 查看入参和出参大小，watch arthas.ArthasDemo addHashSetThread '{params[0],returnObj.size}'
     * 查看入参和出参中是否包含 'count10'，watch arthas.ArthasDemo addHashSetThread '{params[0],returnObj.contains("count10")}'
     * 查看入参和出参（出参 toString），watch arthas.ArthasDemo addHashSetThread '{params[0],returnObj.toString()}'
     *
     */
    public static void addHashSetThread() {
        // 初始化常量
        new Thread(() -> {
            int count = 0;
            while (true) {
                try {
                    hashSet.add("count" + count);
                    Thread.sleep(10000);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void cpu() {
        cpuHigh();
        cpuNormal();
    }

    /**
     * 极度消耗CPU的线程，
     * 定义了线程池大小为1 的线程池，然后在该方法里提交了一个线程，在 thread方法再次提交了一个线程，后面的这个线程因为线程池已
     * 满，会阻塞下来。
     * 使用 thread | grep pool 命令查看线程池里线程信息。
     */
    private static void cpuHigh() {
        Thread thread = new Thread(() -> {
            while (true) {
                System.out.println("cpu start 100");
            }
        });
        // 添加到线程
        executorService.submit(thread);
    }

    /**
     * 普通消耗CPU的线程
     */
    private static void cpuNormal() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    System.out.println("cpu start");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 模拟线程阻塞,向已经满了的线程池提交线程
     */
    private static void thread() {
        Thread thread = new Thread(() -> {
            while (true) {
                System.out.println("thread start");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // 添加到线程
        executorService.submit(thread);
    }

    /**
     * 死锁
     * 使用 thread -b 命令查看直接定位到死锁信息。
     */
    private static void deadThread() {

        /** 创建资源 */
        Object resourceA = new Object();
        Object resourceB = new Object();

        // 创建线程
        Thread threadA = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println(Thread.currentThread() + " get ResourceA");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "waiting get resourceB");
                synchronized (resourceB) {
                    System.out.println(Thread.currentThread() + " get resourceB");
                }
            }
        });

        Thread threadB = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println(Thread.currentThread() + " get ResourceB");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "waiting get resourceA");
                synchronized (resourceA) {
                    System.out.println(Thread.currentThread() + " get resourceA");
                }
            }
        });
        threadA.start();
        threadB.start();
    }

}
