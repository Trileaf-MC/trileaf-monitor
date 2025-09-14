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