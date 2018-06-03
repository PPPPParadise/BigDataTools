Reference
```
twitterStream:
http://blog.csdn.net/dreamd1987/article/details/8269089

Mysql:
http://www.jianshu.com/p/fd3aae701db9
http://blog.csdn.net/lizhe_dashuju/article/details/72377105
http://www.cnblogs.com/aniuer/archive/2012/09/10/2679241.html

DataObject design:
http://blog.csdn.net/zhuangm_888/article/details/50476423
```

Environment setting:
```
Mysql:
create twitter;
use twitter;
create table user(iduser bigint,numFollowers bigint,numFriends bigint,createdAt date);
create table tweet(idTweet bigint,text text,date date,iduser bigint);
```

How to Use
```
1.check your Mysql environment
2.run TwitterDataStream.java
3.check data on your mysql
4.run JungLearning.java
```

Todo
```
1、Multi-threads control
2、Configuration-simple, but flexible
```
