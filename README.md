# mufy 🎥

A CLI tool to generate gifs from your favorite movie

## Install ⚙️

Copy-paste below command into your terminal to install latest version

```shell script
wget "https://raw.githubusercontent.com/theapache64/mufy/master/install.sh" -q --show-progress -O install.sh && sh install.sh && source ~/.bashrc
```

## Usage 💻

```shell script
mufy -i movie.mp4 -n 10 -k "what?" -c "WHAT!!!"
```

- `-i` : Input file
- `-k` : Keywords. If not given **all words** in the input file will be considered as keywords. (optional)
- `-n` : Number of gifs to be generated, default is `-1 (maximum)` (optional)
- `-c` : Caption to be displayed on the GIF. By default, passed keyword will be displayed.


## Author ✍️

- theapache64

