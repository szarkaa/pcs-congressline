(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendInvoiceEmailDialog', sendInvoiceEmailDialog);

    sendInvoiceEmailDialog.$inject = ['$uibModal'];

    function sendInvoiceEmailDialog($uibModal) {
        var directive = {
            transclude: true,
            restrict: 'EA',
            template: '<a ng-click="open()" ng-transclude></a>',
            replace: true,
            scope: {
                invoice: "@",
                email: "@"
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            scope.open = function() {
                $uibModal.open({
                    templateUrl: 'app/services/invoice/send-invoice-email-dialog.html',
                    controller: 'SendInvoiceEmailDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        invoiceEmail: function(){
                            return {invoice: scope.invoice, email: scope.email};
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('invoice');
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                }).result.then(function() {

                }, function() {

                });
            };
        }
    }
})();
