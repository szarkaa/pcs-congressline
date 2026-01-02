(function() {
    'use strict';

    angular
        .module('pcsApp')
        .factory('notificationInterceptor', notificationInterceptor);

    notificationInterceptor.$inject = ['$q', '$injector'];

    function notificationInterceptor ($q, $injector) {
        var service = {
            response: response
        };

        return service;

        function response (response) {
            if (response) {
                var alertKey = response.headers('X-pcsApp-alert');
                if (angular.isString(alertKey)) {
                    var AlertService = $injector.get('AlertService');
                    AlertService.success(alertKey, {param: response.headers('X-pcsApp-params')});
                }
            }
            return response;
        }
    }
})();
