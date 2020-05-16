echo "Downloading main JAR..." &&
wget -q "https://github.com/theapache64/mufy/releases/latest/download/mufy.main.jar" -O "mufy.main.jar" --show-progress &&
# cp /home/theapache64/Documents/projects/mufy/mufy.main.jar mufy.main.jar &&

echo "Downloading assets" &&
wget -q "https://raw.githubusercontent.com/theapache64/mufy/master/assets/impact.ttf" -O "impact.ttf" --show-progress &&


echo "Downloading autocompletion script..." &&
wget -q "https://github.com/theapache64/mufy/releases/latest/download/mufy_completion" -O "mufy_completion" --show-progress &&

echo "Moving files to ~/.mufy" &&

mkdir -p ~/.mufy/assets &&
mv mufy.main.jar ~/.mufy/mufy.main.jar &&
mv mufy_completion ~/.mufy/mufy_completion &&
mv impact.ttf ~/.mufy/assets/impact.ttf &&

echo "Installing..." &&
echo "alias mufy='java -jar ~/.mufy/mufy.main.jar'" >> ~/.bashrc &&
echo ". ~/.mufy/mufy_completion" >> ~/.bashrc &&

echo "Done"