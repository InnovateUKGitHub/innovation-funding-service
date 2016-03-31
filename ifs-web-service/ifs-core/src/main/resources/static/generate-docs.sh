if [ -d "./styleguide" ]; then
	rm -r ./styleguide
fi

#generate css
compass clean -c config-docs.rb && 
compass compile -c config-docs.rb 

#generate styleguide and make the govuk-components.html the index.html
./node_modules/.bin/styledocco -o styleguide -n "Innovation Funding Service styleguide" css-docs \
	--include ./js/vendor/modernizr/modernizr.js \
	--include ./js/dest/hallo.min.js \
	--include ./js/dest/govuk.min.js \
	--include ./js/dest/ifs.min.js \
	--verbose &&
rm ./styleguide/index.html && 
cp ./styleguide/govuk-elements.html ./styleguide/index.html  

#copy the images of the styleguide to the docs folder and also the javascript that is used by it.
cp -r ./sass-docs/images ./styleguide
cp -r ./sass-docs/js ./styleguide

#OSX open the styleguide with chrome 
#FILEPATH=`pwd`
#open -a "Google Chrome"  "$FILEPATH/styleguide/index.html"
#
#open -a "Google Chrome"  "http://localhost:8080/styleguide/index.html"

#remove css-docs folder
if [ -d "./css-docs" ]; then
	rm -r ./css-docs
fi