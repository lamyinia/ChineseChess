每一个java运行程序都是一个虚拟机



@param 核心线程数量，最大线程数，非核心线程空闲存活时间、时间单位、阻塞队列、线程工厂、拒绝策略



ExecutorService gameThreadPool  = new ThreadPoolExecutor(int, int, long, TimeUnit, 

BlockingQueue<Runnable>,

ThreadFactory,

RejectedExecutionHandler);



线程套线程的情况



参考文献：

https://www.sohu.com/a/215163127_664564 @象棋英文名

https://blog.csdn.net/wts563540/article/details/122435631 @并发相关

https://www.cnblogs.com/sunweiye/p/11172028.html @并发相关



参考视频：

https://www.bilibili.com/video/BV1ayoLY7EhN?spm_id_from=333.788.videopod.episodes&vd_source=f81330d449720da32c31ef6d2621d39a&p=20 @并发相关