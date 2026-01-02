(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('ShowNavSendingStatusController',ShowNavSendingStatusController);

    ShowNavSendingStatusController.$inject = ['$uibModalInstance', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'invoiceNavStatusArray'];

    function ShowNavSendingStatusController($uibModalInstance, DTOptionsBuilder, DTColumnDefBuilder,invoiceNavStatusArray) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [0, 'asc']);
        vm.dtOptions.withOption('scrollX', true);

        vm.invoiceNavStatusArray = invoiceNavStatusArray;
        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
