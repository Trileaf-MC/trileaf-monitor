## 1. clone 仓库

```bash
git clone 仓库地址
```

```bash
git clone https://github.com/Trileaf-MC/trileaf-monitor.git
```

你要记住塞在了哪里

现在没有文件，是因为分支（branch）不对

## 2. 切换分支

```bash
git checkout 分支名
```
分支名称怎么获取？

看起来最新的分支名称是 1.20.1

所以你懂的

```bash
git checkout 1.20.1
```

easy

可以看到，文件都来了


## 3。 提交代码

> 你写好的东西，怎么给别人看到？

```bash
git add .
```

```bash
git commit -m "提交信息"
```

```bash
git push
```

就只需要这三步

### 1. git add .
！！先点这个加号，这相当于 git add .

### 2. git commit -m "提交信息"
当你点击“提交”，这相当于 git commit -m "提交信息"

### 3. git push
当你点这个同步更改，相当于 git push
这一步，会真正的推送到 github 云端，你就可以在 github 上看到你的代码了  

我知道了，没设置代理

```bash
git config --global http.proxy http://127.0.0.1:7890
```

## 4. 你自己的分支
新建分支：

```bash
git checkout -b 分支名
```
这命令等效于在这里点一下新建分支

```bash
# 查看自己在哪一个分支
git branch
```
新建成功，可以推送到远程了

git push 或者这里点一下

git fetch 是用来从远程下载别人推送的东西的，但是 trae 刚才自动帮你做了，所以你看不到

可以看到已经有你的分支了，以后你就在这个分支上开发即可

现在，你可以看到左边绿色的条，这代表着新增的内容。

现在，请你把新增的这些内容推送到 github ，使得网页上也能看到下面这个笑脸吧，加油

😊
