 (function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalServiceApplicantSelectionDialogController', OptionalServiceApplicantSelectionDialogController);

    OptionalServiceApplicantSelectionDialogController.$inject = ['$timeout', '$state', '$scope', '$stateParams', '$uibModalInstance', 'listFilter', 'OptionalService', 'CongressSelector', 'OptionalServiceApplicantFilter'];

    function OptionalServiceApplicantSelectionDialogController ($timeout, $state, $scope, $stateParams, $uibModalInstance, listFilter, OptionalService, CongressSelector, OptionalServiceApplicantFilter) {
        var vm = this;

        vm.listFilter = listFilter;
        vm.clear = clear;
        vm.select = select;
        OptionalService.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalServices = result;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
            $state.go('optional-service-applicant');
        }

        function select () {
            vm.isSaving = true;
            OptionalServiceApplicantFilter.setOptionalServiceApplicantFilter(vm.listFilter);
            $uibModalInstance.close();
            $state.go('optional-service-applicant', null, {reload: 'optional-service-applicant'});
            vm.isSaving = false;
        }

    }
})();
