(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BackendOnlineRegDeleteAllDialogController',BackendOnlineRegDeleteAllDialogController);

    BackendOnlineRegDeleteAllDialogController.$inject = ['$uibModalInstance', 'onlineRegFilter', 'BackendOnlineReg'];

    function BackendOnlineRegDeleteAllDialogController($uibModalInstance, onlineRegFilter, BackendOnlineReg) {
        var vm = this;

        vm.onlineRegFilter = onlineRegFilter;
        vm.clear = clear;
        vm.confirm = confirm;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function confirm() {
            BackendOnlineReg.deleteAll(createOnlineRegVM(), function () {
                $uibModalInstance.close(true);
            });
        }

        function createOnlineRegVM() {
            var onlineRegVM = {};
            onlineRegVM.onlineRegIdList = [];
            for (var prop in vm.onlineRegFilter.selectedOnlineRegIds) {
                if (vm.onlineRegFilter.selectedOnlineRegIds.hasOwnProperty(prop) && vm.onlineRegFilter.selectedOnlineRegIds[prop]) {
                    onlineRegVM.onlineRegIdList.push(parseInt(prop, 10));
                }
            }
            return onlineRegVM;
        }

    }

})();
