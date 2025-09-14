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

但是，你可以更简单

比如，你要把这篇 git 教程提交上去？

你动过的文件，会是绿色的

这个位置，写信息即可，

！！先点这个加号，这相当于 git add .

当你点击“提交”，这相当于 git commit -m "提交信息"
