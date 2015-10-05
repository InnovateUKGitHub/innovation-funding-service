if [ -d "./docs" ]; then
	rm -r ./docs
fi

#generate css
bundle exec compass clean -c config-docs.rb && 
bundle exec compass compile -c config-docs.rb 

#generate styleguide and make the docs.html the index.html
./node_modules/.bin/styledocco -n "Innovation Funding Service styleguide" css-docs/docs.css \
	--include ./js/vendor/modernizr/modernizr.js \
	--include ./js/ifs.js \
	--include ./js/ifs-finance.js \
	--include ./js/ifs-assessment.js \
	--include ./js/ifs-applications.js \
	--include ./js/vendor/govuk/selection-buttons.js \
	--include ./js/vendor/govuk/application.js \
	--verbose &&
rm ./docs/index.html && 
mv ./docs/docs.html ./docs/index.html  

#copy the images of the styleguide to the docs folder and also the javascript that is used by it.
cp -r ./sass-docs/images ./docs
cp -r ./sass-docs/js ./docs

#OSX open the styleguide with chrome 
#FILEPATH=`pwd`
#open -a "Google Chrome"  "$FILEPATH/docs/index.html"
#
open -a "Google Chrome"  "http://localhost:8080/docs/index.html"

#remove css-docs folder
if [ -d "./css-docs" ]; then
	rm -r ./css-docs
fi