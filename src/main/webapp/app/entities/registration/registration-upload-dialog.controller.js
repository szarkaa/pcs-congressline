(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationUploadDialogController', RegistrationUploadDialogController);

    RegistrationUploadDialogController.$inject = ['$timeout', '$scope', '$state', 'uploadFile', 'DataUtils', 'Registration'];

    function RegistrationUploadDialogController ($timeout, $scope, $state, uploadFile, DataUtils, Registration) {
        var vm = this;

        vm.uploadFile = uploadFile;
        vm.back = back;
        vm.messages = [];
        vm.sendUpload = sendUpload;

        vm.setFile = setFile;
        vm.clearFile = clearFile;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        function setFile($file, pcsFile) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        pcsFile.name = $file.name;
                        pcsFile.file = base64Data;
                        pcsFile.fileContentType = $file.type ? $file.type : 'application/octet-stream';
                    });
                });
            }
        }

        function back() {
            $state.go('registration', null, { reload: true });
        }

        function clearFile(file) {
            file.name = null;
            file.file = null;
            file.fileContentType = null;
        }

        function sendUpload () {
            vm.isSaving = true;
            vm.messages = Registration.upload(vm.uploadFile, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            clearFile(vm.uploadFile);
            $scope.$emit('pcsApp:registrationUpload', result);
        }

        function onSaveError () {
        }


    }
})();
