# 26:59 - 26:02
ffmpeg -y -ss 00:26:59 -t 2 -i input.mp4 -vf \
"scale=512:-1,
drawtext=fontfile=impact.ttf:fontsize=50:fontcolor=white:x=(w-text_w)/2:y=(h-text_h-10):text='WHAT ?':bordercolor=black:borderw=2" \
-c:v libx264 -an cut.mp4 && ffplay -autoexit cut.mp4
