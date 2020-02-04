package arthas;

public class RemoteDEBUG {

    /**
     dump，是指在特定时刻，将整个或部分数据内容记录在另一路径中。存储的目的通常是为了防止发生错误。或者将具备可读格式的数据从主要或辅助储存体复制至外部媒介。

     远程debug，远程调试：
     JAVA 支持调试功能，本身提供了一个简单的调试工具JDB，支持设置断点及线程级的调试同时，不同的JVM通过接口的协议联系，本地的Java文件在远程JVM建立联系和通
     信。以下是 Intellij IDEA 远程调试的教程汇总和原理解释，要求本地代码和远程 Tomcat 的代码要一致。
     1，在 setting --> edit configuration --> 添加 Remote。
     2，Debbuger mode = attach，Transport = socket，host = 远程主机 IP，port = 远程端口号。
     3，服务器端开启调试模式，增加JVM启动参数，以支持远程调试。服务器端 tomcat 的 catalina.sh 文件 --> 在第一行添加参数配置 --> CATALINA_OPTS="-Xdebug
     Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=本端口号"。
     4，然后打开IDEA，在代码程序上打上断点，运行 remote 模式。然后调用远程机器上的服务即可进入断点。

     各参数解释：
     -Xdebug，是通知 JVM 工作在 DEBUG 模式下。
     -Xrunjdwp，是通知 JVM 使用（java debug wire protocol）来运行调试环境。该参数同时了一系列的调试选项：
     transport，指定了调试数据的传送方式，dt_socket是指用SOCKET模式，dt_shmem是指用共享内存方式，并且 dt_shmem只适用于Windows平台。
     server，是指目标应用程序（server 端）是否监听将要连接的调试器应用程序（本地程序）。若值为 n，则它将连接到特定地址上的调试器应用程序。
     onthrow，指明当产生该类型的 Exception 时，JVM 就会中断下来，进行调式。该参数可选。
     launch，指明当JVM被中断下来时，执行的可执行程序。该参数可选。
     suspend，指明是否在调试客户端建立起来后，再执行JVM。
     onuncaught(=y或n)，指明出现 uncaught exception 后，是否中断JVM的执行。该参数可选。

     传输模式（默认为Socket）：Socket 用于 linux，shared memory 用于 windows。
     调试模式（默认为Attach）：
     Attach ：此种模式下，调试服务端（被调试远程运行的机器）启动一个端口等待我们（调试客户端）去连接;
     Listen ：此种模式下，是我们（调试客户端）去监听一个端口，当调试服务端准备好了，就会进行连接。

     远程调试原理：
     Java程序的执行过程分为以下几个步骤：Java的文件 --> 编译生成的类文件（class文件）--> JVM加载类文件 --> JVM运行类字节码文件 --> JVM翻译器翻译成各个机器认
     识的不同的机器码。
     Java程序统一以字节码的形式在JVM中运行，不同平台的虚拟机都统一使用这种相同的程序存储格式。因为都是类字节码文件，只要本地代码和远程服务器上的类文件相同，
     两个JVM通过调试协议进行通信（例如通过插座在同一个端口进行通信），另外需要注意，被调试的服务器需要开启调试模式，服务器端的代码和本地代码必须保持一致，否
     则会造成断点无法进入的问题。
     两种调试模式其实质还是JVM，只要确保本地的Java的源代码与目标应用程序一致，本地的Java的的的的源码就可以用插座连接到远端的JVM，进而执行调试。因此在这种
     插 座连接模式下，本地只需要有源码，本地的Java的应用程序根本不用启动。
     */
}
