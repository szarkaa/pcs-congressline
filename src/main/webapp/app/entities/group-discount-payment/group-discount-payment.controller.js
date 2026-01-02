(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountPaymentController', GroupDiscountPaymentController);

    GroupDiscountPaymentController.$inject = ['$scope', '$state', 'GroupDiscountPayment', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function GroupDiscountPaymentController ($scope, $state, GroupDiscountPayment, DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.groupDiscountPayments = [];

        loadAll();

        function loadAll() {
            GroupDiscountPayment.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.groupDiscountPayments = result;
            });
        }
    }
})();
