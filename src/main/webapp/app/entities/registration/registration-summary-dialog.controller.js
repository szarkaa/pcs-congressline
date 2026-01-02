'use strict';

angular.module('pcsApp').controller('RegistrationSummaryDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'summary', function($scope, $stateParams, $uibModalInstance, summary) {
        var vm = this;
        vm.summary = summary;
        vm.clear = clear;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }
}]);
