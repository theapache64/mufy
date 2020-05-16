# 26:59 - 26:02
ffmpeg -y -ss 328.161 -t 2 -i movie.mp4 -vf \
"scale=512:-1,
drawtext=fontfile=impact.ttf:fontsize=50:fontcolor=white:x=(w-text_w)/2:y=(h-text_h-10):text='WHAT ?':bordercolor=black:borderw=2" \
-c:v libx264 -an cut.mp4 && ffplay -autoexit cut.mp4 && ffmpeg -y -i cut.mp4 -vf "scale=256:-1" cut.gif
