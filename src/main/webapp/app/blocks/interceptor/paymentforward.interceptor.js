(function() {
    'use strict';

    angular
        .module('pcsApp')
        .factory('paymentForwardInterceptor', paymentForwardInterceptor);

    paymentForwardInterceptor.$inject = ['$q', '$injector'];

    function paymentForwardInterceptor ($q, $injector) {
        var service = {
            response: response
        };

        return service;

        function response (response) {
            var redirectHeader = response.headers('X-pcsApp-payment-redirect');
            if (angular.isString(redirectHeader)) {
                // console.log(redirectHeader);
                if (redirectHeader) {
                    document.location.href = redirectHeader;
                    return;
                }
            }
            return response;
        }
    }
})();
