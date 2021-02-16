NIO组成部分：
1、缓冲区Buffer
   所有数据都是通过缓冲区进行处理的，读数据时，直接读到缓冲区；写数据时，先写到缓冲区。
   任何时候访问NIO的数据，都是通过缓冲区进行访问的。
   
2、通道 Channel
   通道与流不同，通道是双工的，可用于读写；但流只能在一个方向上移动，只能读或写。
   
3、多路复用器 Selector
   （1）NIO的基础。Selector会不断轮询注册在该 Selector 上的 Channel，若某个 Channel 上面发生读、写事件，
   这个 Channel 就处于就绪状态，然后被 Selector 轮询出来，然后通过 SelectionKey 获取就绪的 Channel 集合，
   进行后续的I/O操作。
   （2）JDK使用了 epoll()，所以没有最大连接句柄的限制。所以可以让一个 Selector 负责上万个客户端。