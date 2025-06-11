

由于TCP是全双工，对于 Socket 和 ServerSocket，完全有可能会出现两边同时向对方发送消息的情况

ServerSocket本身并不用于数据通信，它只是用来监听端口并接受连接。一旦接受了一个连接，就会创建一个新的Socket对象用于和该客户端通信。服务器端通常会为每个连接的客户端创建一个新的线程（或使用线程池）来处理这个Socket的通信。





每一个java运行程序都是一个虚拟机



@param 核心线程数量，最大线程数，非核心线程空闲存活时间、时间单位、阻塞队列、线程工厂、拒绝策略



ExecutorService gameThreadPool  = new ThreadPoolExecutor(int, int, long, TimeUnit, 

BlockingQueue<Runnable>,

ThreadFactory,

RejectedExecutionHandler);



线程套线程的情况

观察者模式：

主动和被动的关系、行为和消息的关系、主题和观察者的关系、事件模型和事件驱动





参考文献：

https://www.sohu.com/a/215163127_664564 @象棋英文名

https://blog.csdn.net/wts563540/article/details/122435631 @并发相关

https://www.cnblogs.com/sunweiye/p/11172028.html @并发相关



参考视频：

https://www.bilibili.com/video/BV1ayoLY7EhN?spm_id_from=333.788.videopod.episodes&vd_source=f81330d449720da32c31ef6d2621d39a&p=20 @并发相关