(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceHistoryDetailController', GroupDiscountInvoiceHistoryDetailController);

    GroupDiscountInvoiceHistoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'GroupDiscountInvoiceHistory', 'GroupDiscountInvoice'];

    function GroupDiscountInvoiceHistoryDetailController($scope, $rootScope, $stateParams, previousState, entity, GroupDiscountInvoiceHistory, GroupDiscountInvoice) {
        var vm = this;

        vm.groupDiscountInvoiceHistory = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pcsApp:groupDiscountInvoiceHistoryUpdate', function(event, result) {
            vm.groupDiscountInvoiceHistory = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
