(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceItemDialogController', MiscInvoiceItemDialogController);

    MiscInvoiceItemDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity',
        'MiscService', 'miscInvoice', 'CongressSelector', 'selectedCurrency', 'Rate'];

    function MiscInvoiceItemDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity,
        MiscService, miscInvoice, CongressSelector, selectedCurrency, Rate) {
        var vm = this;

        vm.miscInvoice = miscInvoice;
        vm.miscInvoiceItem = entity;
        vm.clear = clear;
        vm.save = save;
        vm.getSelectedCurrency = getSelectedCurrency;
        vm.selectedCurrency = selectedCurrency;

        MiscService.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function (result) {
            vm.miscServices = result;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function uuid() {
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            }
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                s4() + '-' + s4() + s4() + s4();
        }


        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.miscInvoiceItem.id == null) {
                vm.miscInvoiceItem.id = uuid();
                vm.miscInvoice.miscInvoiceItems.push(vm.miscInvoiceItem);
                vm.selectedCurrency.currency = vm.miscInvoiceItem.miscService.currency.currency;
                if (vm.selectedCurrency.currency !== 'HUF') {
                    Rate.getCurrentRate({currency: vm.selectedCurrency.currency},
                        function (result) {
                            vm.selectedCurrency.hasValidRate = true;
                        },
                        function (result) {
                            vm.selectedCurrency.hasValidRate = false;
                        }
                    );
                }
            }

            $uibModalInstance.close(true);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:miscInvoiceItemUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function getSelectedCurrency() {
            return vm.selectedCurrency.currency ? vm.selectedCurrency.currency : '';
        }
    }
})();
