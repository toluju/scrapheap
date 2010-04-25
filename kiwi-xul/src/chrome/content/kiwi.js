jQuery.fn.setVal = function(value) {
  this[0].setAttribute("value", value);
  return this;
}

jQuery.create = function(name) {
  var node = document.createElement(name);
  return jQuery.fn.init(node);
}

function loadTweets() {
  $("#tweetlist").empty();

  var username = $("#username").val();
  var password = $("#password").val();

  $.ajax({
    type: "GET",
    url: "http://twitter.com/statuses/friends_timeline.json",
    dataType: "json",
    username: username,
    password: password,
    success: function(msg) {
      var doc = $("#tweetbrowser")[0].contentDocument;
      var list = $("#tweetlist", doc);
  
      for (m in msg) {
        var tweet = msg[m];
        $("<li>" + tweet.text + "</li>", doc).appendTo(list);
      }
    }
  });
}
