(function() {
    'use strict';

    angular
        .module('pcsApp')
        .factory('InvoiceUtils', InvoiceUtils);

    InvoiceUtils.$inject = ['$http'];

    function InvoiceUtils ($http) {
        return {
            hasValidRate: function (currency) {
                return $http.get('api/registrations/invoices/rates/' + currency);
            }
        };
    }
})();
