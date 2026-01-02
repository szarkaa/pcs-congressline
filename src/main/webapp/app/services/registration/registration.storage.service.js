(function() {
    'use strict';

    angular.module('pcsApp')
        .factory('RegIdStorage', RegIdStorage);

    RegIdStorage.$inject = ['$window'];

    function RegIdStorage ($window) {
        var lastRegId = null;

        return {
            setLastRegId: function (id) {
                if (id) {
                    lastRegId = id;
                    $window.sessionStorage.setItem('last-reg-id', lastRegId);
                }
                else {
                    lastRegId = null;
                    $window.sessionStorage.removeItem('last-reg-id');
                }

            },
            getLastRegId: function () {
                return lastRegId;
            }
        };
    }
})();
