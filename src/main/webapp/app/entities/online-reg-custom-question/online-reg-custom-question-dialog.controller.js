(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegCustomQuestionDialogController', OnlineRegCustomQuestionDialogController);

    OnlineRegCustomQuestionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'OnlineRegCustomQuestion', 'Congress'];

    function OnlineRegCustomQuestionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, OnlineRegCustomQuestion, Congress) {
        var vm = this;

        vm.onlineRegCustomQuestion = entity;
        vm.clear = clear;
        vm.save = save;
        vm.appendSelectableValue = appendSelectableValue;
        vm.deleteFromSelectableValues = deleteFromSelectableValues;

        Congress.getOnlineRegCurrenciesByCongressId({ id: vm.onlineRegCustomQuestion.congress.id } , function (result) {
            vm.currencies = result;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.onlineRegCustomQuestion.id !== null) {
                OnlineRegCustomQuestion.update(vm.onlineRegCustomQuestion, onSaveSuccess, onSaveError);
            } else {
                OnlineRegCustomQuestion.save(vm.onlineRegCustomQuestion, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:onlineRegCustomQuestionUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

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
    }
})();
