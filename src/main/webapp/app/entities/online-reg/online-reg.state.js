(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('online-reg', {
                parent: 'base',
                url: '/registration/online/form/{uuid}/{currency}/{language}',
                data: {
                    authorities: [],
                    pageTitle: 'pcsApp.onlineReg.home.title'
                },
                views: {
                    'base@': {
                        templateUrl: 'app/entities/online-reg/online-reg-form.html',
                        controller: 'OnlineRegController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    congress: ['OnlineReg', '$state', '$stateParams', function (OnlineReg, $state, $stateParams) {
                        return OnlineReg.getCongress({uuid: $stateParams.uuid}).$promise.then(function (result) {
                            if (result.onlineRegCurrencies.indexOf($stateParams.currency) === -1) {
                                $state.go('online-reg-error', {
                                    uuid: $stateParams.uuid,
                                    currency: $stateParams.currency,
                                    language: $stateParams.language,
                                    errorId: 'currency-not-exist'
                                }, {reload: true});
                            }
                            else if (result.onlineRegConfig.closed) {
                                $state.go('online-reg-error', {
                                    uuid: $stateParams.uuid,
                                    currency: $stateParams.currency,
                                    language: $stateParams.language,
                                    errorId: 'congress-closed'
                                }, {reload: true});
                            }
                            else {
                                return result;
                            }
                        }, function (error) {
                            return $state.go('online-reg-error', {
                                uuid: $stateParams.uuid,
                                currency: $stateParams.currency,
                                language: $stateParams.language,
                                errorId: 'congress-not-exist'
                            }, {reload: true});
                        });
                    }],
                    registration: ['$stateParams', function ($stateParams) {
                        return { // in clear function as well!!!
                            title: null,
                            lastName: null,
                            firstName: null,
                            position: null,
                            department: null,
                            workplace: null,
                            zipCode: null,
                            city: null,
                            country: null,
                            street: null,
                            phone: null,
                            email: null,
                            email2: null,
                            otherData: null,
                            room: null,
                            arrivalDate: null,
                            departureDate: null,
                            roommate: null,
                            roomRemark: null,
                            regType: null,
                            extraRegTypes: {},
                            optionalServices: {},
                            customAnswers: {},
                            paymentMethod: null,
                            cardType: null,
                            checkName: null,
                            checkAddress: null,
                            cardHolderName: null,
                            cardHolderAddress: null,
                            cardNumber: null,
                            cardExpiryMonth: null,
                            cardExpiryYear: null,
                            invoiceName: null,
                            invoiceCountry: null,
                            invoiceZipCode: null,
                            invoiceCity: null,
                            invoiceAddress: null,
                            invoiceReferenceNumber: null,
                            invoiceTaxNumber: null,
                            termsAndConditions: null,
                            discountCode: null,
                            discountPercentage: null,
                            discountType: null,
                            gdpr: null,
                            newsletter: null,
                            currency: null,
                            uuid: $stateParams.uuid
                        };
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('onlineReg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('online-reg-error', {
                parent: 'base',
                url: '/registration/online/{uuid}/{currency}/{language}/error/{errorId}',
                data: {
                    authorities: [],
                    pageTitle: 'pcsApp.onlineReg.home.errorTitle'
                },
                views: {
                    'base@': {
                        templateUrl: 'app/entities/online-reg/online-reg-error.html',
                        controller: 'OnlineRegErrorController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    congress: ['OnlineReg', '$state', '$stateParams', function (OnlineReg, $state, $stateParams) {
                        return OnlineReg.getCongress({uuid: $stateParams.uuid}).$promise.then(function (result) {
                            return result;
                        }, function (error) {
                            return null;
                        });
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('onlineReg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('online-reg-payment-success', {
                parent: 'base',
                url: '/registration/online/payment/result/success?txid',
                params: {
                    txid: null
                },
                data: {
                    authorities: [],
                    pageTitle: 'pcsApp.onlineReg.home.successTitle'
                },
                views: {
                    'base@': {
                        templateUrl: 'app/entities/online-reg/online-reg-payment-success.html',
                        controller: 'OnlineRegPaymentResultController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    paymentResult: ['OnlineReg', '$state', '$stateParams', function (OnlineReg, $state, $stateParams) {
                        return OnlineReg.getPaymentResult({txid: $stateParams.txid}).$promise.then(function (result) {
                            return result;
                        }, function (error) {
                            return null;
                        });
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('onlineReg');
                        return $translate.refresh();
                    }]
                }
            })
            .state('online-reg-payment-failure', {
                parent: 'base',
                url: '/registration/online/payment/result/failure?txid',
                params: {
                    txid: null
                },
                data: {
                    authorities: [],
                    pageTitle: 'pcsApp.onlineReg.home.errorTitle'
                },
                views: {
                    'base@': {
                        templateUrl: 'app/entities/online-reg/online-reg-payment-failure.html',
                        controller: 'OnlineRegPaymentResultController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    paymentResult: ['OnlineReg', '$state', '$stateParams', function (OnlineReg, $state, $stateParams) {
                        return OnlineReg.getPaymentResult({txid: $stateParams.txid}).$promise.then(function (result) {
                            return result;
                        }, function (error) {
                            return null;
                        });
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('onlineReg');
                        return $translate.refresh();
                    }]
                }
            });
    }

})();
