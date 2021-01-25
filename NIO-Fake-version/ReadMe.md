和同步阻塞IO的区别就在于：
1、同步阻塞IO的例子，一个线程只能处理一个连接，而且没有限制，容易导致资源耗尽；
2、伪异步IO的例子，通过线程池的方式管理线程，避免海量的并发接入导致线程耗尽；
3、但是不管怎么样，底层都是同步阻塞的。

原因：
1、因为一旦使用了 Java API 的 Socket，读写都是阻塞的。
2、若输入的时候，会被一直阻塞，直到有数据可读、发生异常、数据读取完毕。
3、若输出的时候，会被一直阻塞，直到所有的字节全部写入完毕，或发生异常。