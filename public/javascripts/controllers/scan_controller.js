app.controller("scanController", function($scope, $log, scanService, $timeout){

    $scope.scans = [];

    // listen socket message
    // pass json object as parameter...
    function handler(info) {

        // copy page value
        var page = info.data.page;

        // reset to zero
        info.data.page = 0;

        // push info with zero page
        $scope.scans.push(info);

        // applay change
        $scope.$apply();

        $timeout(function(){
            info.data.page = page;
            $log.info("timeout >> " + new Date());
        }, 1000);

        $log.info("handler >> " + new Date());
    }

    // when document ready..
    // start listen event from server
    angular.element(document).ready(function(){
        scanService.listen(handler);
    });
});