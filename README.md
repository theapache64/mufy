# mufy 🎥

<a href="https://youtu.be/T_mH0nBAiE0" target="_blank">
<img src="https://raw.githubusercontent.com/theapache64/mufy/master/extras/youtube.png"/>
</a>

A CLI tool to generate gifs from your favorite movie

## Install ⚙️

Copy-paste below command into your terminal to install latest version

```shell script
wget "https://raw.githubusercontent.com/theapache64/mufy/master/install.sh" -q --show-progress -O install.sh && sh install.sh && source ~/.bashrc
```

## Dependency :canoe:

- ffmpeg

## Usage 💻

```
Usage: mufy [-hV] [-kfs] [-c=<caption>] -i=<input> [-n=<numOfGifs>]
            [-k=<keywords>]...
  -c, --caption=<caption>    Caption to be displayed on the GIF. By default,
                               passed keyword will be displayed.
  -h, --help                 Show this help message and exit.
  -i, --input=<input>        Input file to be processed
  -k, --keyword=<keywords>   Keywords (comma-separated)
      -kfs, --keywords-from-subtitle
                             If enabled, keywords will be collected from
                               subtitles (all words)
  -n, --number-of-gifs=<numOfGifs>
                             Number of gifs to be generated.
  -V, --version              Print version information and exit.
```

**Example**
```shell script
mufy -i movie.mp4 -n 10 -k "what?" -c "WHAT!!!"
```

- `-i` : Input file (movie file)
- `-k` : Keywords to be searched. (*optional)
- `-n` : Number of gifs to be generated. Default is `-1 (maximum)` (optional)
- `-c` : Caption to be displayed on the GIF. By default, matched keyword will be displayed.
- `-kfs` : To get **k**eywords **f**rom **s**ubtitle. (*optional)

`*optional` : Both options can't work together. Either one of the option must be given.

## Examples :evergreen_tree:

- To generate maximum GIFs from `movie.mp4`

```shell script
~$ mufy -i movie.mp4 -kfs
```

- To generate **10** GIFs from `movie.mp4` with keyword `what`

```shell script
~$ mufy -i movie.mp4 -n 10 -k 'what'
```

- To generate **10** GIFs from `movie.mp4` with keyword `what` and with caption `WHAT!!!`

```shell script
~$ mufy -i movie.mp4 -n 10 -k 'what' -c 'WHAT!!!'
```

A sample output

![](extras/what.gif)


## Author ✍️

- theapache64

