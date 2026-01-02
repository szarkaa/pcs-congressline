(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('WorkplaceMergeConfirmController', WorkplaceMergeConfirmController);

    WorkplaceMergeConfirmController.$inject = ['$scope', '$uibModalInstance', 'entity', 'selectedWorkplaces', 'Workplace'];

    function WorkplaceMergeConfirmController ($scope, $uibModalInstance, entity, selectedWorkplaces, Workplace) {
        var vm = this;
        vm.selectedWorkplaces = selectedWorkplaces;
        vm.workplace = entity;

        vm.clear = clear;
        vm.confirmMerge = confirmMerge;
        vm.createWorkplaceMergeDTO = createWorkplaceMergeDTO;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmMerge() {
            Workplace.merge(createWorkplaceMergeDTO(), onMergeComplete);
        }

        function onMergeComplete() {
            $uibModalInstance.close(true);
        }

        function createWorkplaceMergeDTO() {
            var workplace = {};
            workplace.workplaceId = vm.workplace.id;
            workplace.mergingWorkplaceIdList = [];

            for (var prop in vm.selectedWorkplaces) {
                if (vm.selectedWorkplaces.hasOwnProperty(prop)) {
                    workplace.mergingWorkplaceIdList.push(parseInt(prop, 10));
                }
            }
            return workplace;
        }
    }
})();
