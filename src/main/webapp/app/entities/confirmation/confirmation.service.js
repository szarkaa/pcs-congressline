(function() {
    'use strict';
    angular
        .module('pcsApp')
        .factory('Confirmation', Confirmation);

    Confirmation.$inject = ['$http', '$timeout'];

    function Confirmation($http, $timeout) {
        return {
            'printConfirmation': function (confirmation) {
                $http.post('api/registrations/confirmation/pdf', confirmation, {responseType: 'arraybuffer'})
                    .success(function(data, status, headers) {
                        var filename = headers('Content-Disposition');
                        filename = filename.split('; filename=')[1].trim();
                        var blob = new Blob([data], {type: 'application/pdf'});
                        var pdfLink = (window.URL || window.webkitURL).createObjectURL(blob);
                        window.open(pdfLink, '_blank');
                        // downloadFile(blob, filename);
                    });
            },
            'sendAndPrintConfirmation': function (confirmation) {
                $http.post('api/registrations/confirmation/send-pdf', confirmation, {responseType: 'arraybuffer'})
                    .success(function(data, status, headers) {
                        var filename = headers('Content-Disposition');
                        filename = filename.split('; filename=')[1].trim();
                        var blob = new Blob([data], {type: 'application/pdf'});
                        var pdfLink = (window.URL || window.webkitURL).createObjectURL(blob);
                        window.open(pdfLink, '_blank');
                        // downloadFile(blob, filename);
                    });
            },
            'sendConfirmationToAll': function (confirmation, onSuccess, onFailure) {
                $http.post('api/registrations/confirmation/send-to-all', confirmation).then(onSuccess, onFailure);
            },
            'sendFinancialNoticeToAll': function (confirmation, onSuccess, onFailure) {
                $http.post('api/registrations/confirmation/notice-to-all', confirmation).then(onSuccess, onFailure);
            },
            'sendAllConfirmationToEmail': function (confirmation, onSuccess, onFailure) {
                $http.post('api/registrations/confirmation/send-all-to-email', confirmation).then(onSuccess, onFailure);
            }
        };

        function downloadFile(blob, filename) {
            if (window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveOrOpenBlob(blob, filename);
            } else {
                var a = document.createElement('a');
                document.body.appendChild(a);
                var url = window.URL.createObjectURL(blob);
                a.href = url;
                a.target = '_blank';
                a.download = filename;
                a.onclick = function () {
                    window.open(this.href, '_blank');
                };
                a.click();
                $timeout(function (){
                    window.URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                });
            }
        }
    }
})();
