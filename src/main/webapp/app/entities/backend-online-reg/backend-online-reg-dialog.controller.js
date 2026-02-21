(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BackendOnlineRegDialogController', BackendOnlineRegDialogController);

    BackendOnlineRegDialogController.$inject = ['$timeout', '$scope', '$state', '$stateParams', 'entity', 'BackendOnlineReg', 'DataUtils'];

    function BackendOnlineRegDialogController ($timeout, $scope, $state, $stateParams, entity, BackendOnlineReg, DataUtils) {
        var vm = this;

        vm.backendOnlineReg = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.downloadAttachment = downloadAttachment;
        vm.cancel = cancel;

        BackendOnlineReg.queryCustomAnswers({id: entity.id}, function (result) {
            vm.customAnswers = result;
        });

        function cancel () {
            $state.go('^');
        }

        function downloadAttachment (id) {
            window.open('/api/backend-online-regs/' + id + '/download', '_blank');
        }
    }
})();
