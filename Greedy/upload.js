console.log('Loading a web page');
var page = require('webpage').create();
page.customHeaders={'Authorization': 'Basic '+btoa('Guidoffrey:baratheon')};
var url = 'http://swoc.jdub.nl/#/login';
page.open(url, function (status) {
  //Page is loaded!
  page.evaluate(function() {
    
  });
  phantom.exit();
});