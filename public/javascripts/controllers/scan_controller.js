app.controller("scanController", function($scope, $log, scanService){

    $scope.scans = [];

    // listen socket message
    // pass json object as parameter...
    function handler(info) {
        $scope.scans.push(info);
        $scope.$apply();
    }

    // when document ready..
    // start listen event from server
    angular.element(document).ready(function(){
        scanService.listen(handler);
    });
});