(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegConfigDialogController', OnlineRegConfigDialogController);

    OnlineRegConfigDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Congress', 'Country'];

    function OnlineRegConfigDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Congress, Country) {
        var vm = this;

        vm.countries = Country.query();
        vm.onlineRegConfig = entity;
        vm.clear = clear;
        vm.save = save;
        vm.setNormalFile = setNormalFile;
        vm.clearNormalFile = clearNormalFile;
        vm.appendSelectableValue = appendSelectableValue;
        vm.deleteFromSelectableValues = deleteFromSelectableValues;
        vm.noPaymentRequiredChanged = noPaymentRequiredChanged;
        vm.paymentSupplierChanged = paymentSupplierChanged;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            Congress.updateOnlineRegConfig(vm.onlineRegConfig, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:congressUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.summerNoteConfig = {
            height: 150,
            toolbar: [
                ['style', ['bold', 'italic', 'underline', 'superscript', 'subscript', 'strikethrough', 'clear']],
                ['alignment', ['ul', 'ol']]
            ]
        };

        function appendSelectableValue(values, idx) {
            var newValues = [];
            for (var i = 0; i < values.length; i++) {
                newValues.push(values[i]);
            }
            newValues.splice(idx, 0, "");

            values.splice(0, values.length);
            for (i = 0; i < newValues.length; i++) {
                values.push(newValues[i]);
            }
        }

        function deleteFromSelectableValues(values, idx) {
            values.splice(idx, 1);
        }

        function setNormalFile($file, onlineRegConfig) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        onlineRegConfig.headerNormalName = $file.name;
                        onlineRegConfig.headerNormalContentType = $file.type ? $file.type : 'application/octet-stream';
                        onlineRegConfig.headerNormalFile  = base64Data;
                    });
                });
            }
        }

        function clearNormalFile() {
            vm.onlineRegConfig.headerNormalName = null;
            vm.onlineRegConfig.headerNormalContentType = null;
            vm.onlineRegConfig.headerNormalFile = null;
        }

        function noPaymentRequiredChanged() {
            if (vm.onlineRegConfig.noPaymentRequired) {
                vm.onlineRegConfig.paymentSupplier = null;
                vm.onlineRegConfig.stripeSecretKey = null;
                vm.onlineRegConfig.stripePublicKey = null;
            }
        }

        function paymentSupplierChanged() {
            if (vm.onlineRegConfig.paymentSupplier === 'STRIPE') {
                vm.onlineRegConfig.bankTransferVisible = false;
                vm.onlineRegConfig.checkVisible = false;
                vm.onlineRegConfig.creditCardVisible = false;
                vm.onlineRegConfig.bankTransferInfoHu = null;
                vm.onlineRegConfig.bankTransferInfoEn = null;
                vm.onlineRegConfig.bankTransferInfoEs = null;
                vm.onlineRegConfig.bankTransferInfoPt = null;
                vm.onlineRegConfig.billingRemarkHu = null;
                vm.onlineRegConfig.billingRemarkEn = null;
                vm.onlineRegConfig.billingRemarkEs = null;
                vm.onlineRegConfig.billingRemarkPt = null;
            }
        }

    }
})();
