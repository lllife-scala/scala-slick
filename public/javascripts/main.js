// Main module
var app = angular.module("bupaApplication", ['ngAnimate']);


//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// Configuration
// * baseUrl
// * xxx
app.factory("configService", function () {
    var service = {}
    service.baseUrl = "http://10.0.0.67:9000"
    service.baseSocketUrl = service.baseUrl.replace("http", "ws");
    return service;
});

app.factory("scanService", function ($log, $q, $rootScope, configService) {
    // web socket uri.
    // replace http:// with ws://
    var url = configService.baseSocketUrl + "/socket/connect";

    var service = {};
    service.listen = function (callback) {
        var ws = new WebSocket(url);
        ws.onopen = function () {
            $log.info(">> ws.open ");
        }
        ws.onmessage = function (text) {
            $log.info(">> ws.message >> " + text);
            callback(JSON.parse(text.data));
        }
    };
    return service;
});

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
app.directive("scan", function(){
    return {
        retrict: "E",
        templateUrl: "/assets/views/scan.html",
        replace: true
    };
});

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
app.filter("reverse", function(){
    return function(items){
        return items.slice().reverse()
    };
});

