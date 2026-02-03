(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('group-discount-item', {
                parent: 'group-discount',
                url: '/group-discount-item',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.groupDiscountItem.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/group-discount-item/group-discount-items.html',
                        controller: 'GroupDiscountItemController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('groupDiscountItem');
                        $translatePartialLoader.addPart('groupDiscountInvoice');
                        $translatePartialLoader.addPart('chargeableItemType');
                        $translatePartialLoader.addPart('optionalText');
                        $translatePartialLoader.addPart('navVatCategory');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('group-discount-item.selection', {
                parent: 'group-discount-item',
                url: '/select',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'groupDiscountItemFilter', function ($stateParams, $state, $uibModal, groupDiscountItemFilter) {
                    $uibModal.open({
                        templateUrl: 'app/entities/group-discount-item/group-discount-item-selection-dialog.html',
                        controller: 'GroupDiscountItemSelectionDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            groupDiscountItemFilter: function () {
                                return groupDiscountItemFilter;
                            }
                        }
                    });
                }]
            })
            .state('group-discount-item.setup', {
                parent: 'group-discount-item',
                url: '/invoice/setup',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/group-discount-item/group-discount-item-invoice-setup-dialog.html',
                        controller: 'GroupDiscountItemInvoiceSetupDialogController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    invoice: ['CongressSelector', 'groupDiscountItemFilter', function (CongressSelector, groupDiscountItemFilter) {
                        return {
                            invoiceNumber: null,
                            stornoInvoiceNumber: null,
                            name: groupDiscountItemFilter.payingGroup ? groupDiscountItemFilter.payingGroup.name : null,
                            taxNumber: groupDiscountItemFilter.payingGroup ? groupDiscountItemFilter.payingGroup.taxNumber : null,
                            city: groupDiscountItemFilter.payingGroup ? groupDiscountItemFilter.payingGroup.city : null,
                            zipCode: groupDiscountItemFilter.payingGroup ? groupDiscountItemFilter.payingGroup.zipCode : null,
                            street: groupDiscountItemFilter.payingGroup ? groupDiscountItemFilter.payingGroup.street : null,
                            country: groupDiscountItemFilter.payingGroup ? groupDiscountItemFilter.payingGroup.country.code : null,
                            optionalText: null,
                            startDate: new Date(CongressSelector.getSelectedCongress().startDate),
                            endDate: new Date(CongressSelector.getSelectedCongress().endDate),
                            dateOfFulfilment: new Date(),
                            paymentDeadline: (new Date()).setDate((new Date()).getDate() + 10),
                            billingMethod: 'TRANSFER',
                            language: 'hu',
                            navVatCategory: null,
                            bankName: null,
                            bankAccount: null,
                            swiftCode: null,
                            storno: null,
                            stornired: null,
                            createdDate: null,
                            dateOfGrougPayment: null,
                            id: null,
                            payingGroup: null
                        };
                    }]
                }
            })
            .state('group-discount-item.optional-text-new', {
                parent: 'group-discount-item',
                url: '/optional-text/new',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function ($stateParams, $state, $uibModal, CongressSelector) {
                    $uibModal.open({
                        templateUrl: 'app/entities/optional-text/optional-text-dialog.html',
                        controller: 'OptionalTextDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    optionalText: null,
                                    id: null,
                                    congress: CongressSelector.getSelectedCongress()
                                };
                            }
                        }
                    }).result.then(function (result) {
                            $state.go('group-discount-item.setup', {reload: true});
                        }, function () {
                            $state.go('group-discount-item.setup');
                        })
                }]
            });

    }


})();
