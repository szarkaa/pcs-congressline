(function() {
    'use strict';

    angular
        .module('pcsApp')
        .run(stateChangeStart)
        .config(stateConfig);

    stateChangeStart.$inject = ['$rootScope', '$state', 'Registration', 'CongressSelector', 'RegIdStorage'];
    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('registration', {
            parent: 'administration',
            url: '/registrations/{registrationId}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.registration.detail.title'
            },
            onEnter: ['$stateParams', 'RegIdStorage', function($stateParams, RegIdStorage) {
                RegIdStorage.setLastRegId($stateParams.registrationId);
            }],
            views: {
                'content@base': {
                    templateUrl: 'app/entities/registration/registrations.html',
                    controller: 'RegistrationController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                registrationArray: ['CongressSelector', 'Registration', function(CongressSelector, Registration) {
                    return Registration.queryVMByCongress({id : CongressSelector.getSelectedCongress().id});
                }],
                registration: ['$stateParams', 'Registration', function($stateParams, Registration) {
                    if($stateParams.registrationId && $stateParams.registrationId != 0) {
                        return Registration.get({id : $stateParams.registrationId}).$promise;
                    }
                    else {
                        return null;
                    }
                }],
                registrationRegistrationTypes: ['$stateParams', 'RegistrationRegistrationType', function ($stateParams, RegistrationRegistrationType) {
                    return RegistrationRegistrationType.queryVMByRegistrationId({id: $stateParams.registrationId}).$promise;
                }],
                roomReservations: ['$stateParams', 'RoomReservation', function ($stateParams, RoomReservation) {
                    return RoomReservation.queryVMByRegistrationId({id: $stateParams.registrationId}).$promise;
                }],
                orderedOptionalServices: ['$stateParams', 'OrderedOptionalService', function ($stateParams, OrderedOptionalService) {
                    return OrderedOptionalService.queryDTOByRegistrationId({id: $stateParams.registrationId}).$promise;
                }],
                chargedServices: ['$stateParams', 'ChargedService', function ($stateParams, ChargedService) {
                    return ChargedService.queryByRegistrationId({id: $stateParams.registrationId}).$promise;
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('registration');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('registrationRegistrationType');
                    $translatePartialLoader.addPart('roomReservation');
                    $translatePartialLoader.addPart('roomReservationRegistration');
                    $translatePartialLoader.addPart('chargedService');
                    $translatePartialLoader.addPart('orderedOptionalService');
                    $translatePartialLoader.addPart('workplace');
                    $translatePartialLoader.addPart('country');
                    $translatePartialLoader.addPart('chargedServicePaymentMode');
                    $translatePartialLoader.addPart('chargeableItemType');
                    return $translate.refresh();
                }]
            }
        })
        .state('registration.new', {
            parent: 'registration',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/registration/registration-dialog.html',
                    controller: 'RegistrationDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                registration: ['CongressSelector', function (CongressSelector) {
                    return {
                        id: null,
                        regId: null,
                        lastName: null,
                        firstName: null,
                        shortName: null,
                        title: null,
                        position: null,
                        otherData: null,
                        department: null,
                        countryId: null,
                        zipCode: null,
                        city: null,
                        street: null,
                        phone: null,
                        email: null,
                        fax: null,
                        invoiceName: null,
                        invoiceCountryId: null,
                        invoiceZipCode: null,
                        invoiceCity: null,
                        invoiceAddress: null,
                        invoiceTaxNumber: null,
                        dateOfApp: new Date(),
                        remark: null,
                        onSpot: null,
                        cancelled: null,
                        presenter: null,
                        closed: null,
                        etiquette: null,
                        workplaceId: null,
                        congressId: CongressSelector.getSelectedCongress().id
                    };
                }],
                workplaces: ['Workplace', 'CongressSelector', function(Workplace, CongressSelector) {
                    return Workplace.queryByCongress({id: CongressSelector.getSelectedCongress().id});
                }],
                countries: ['Country', function(Country) {
                    return Country.query();
                }]
            }
        })
        .state('registration.edit', {
            parent: 'registration',
            url: '/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/registration/registration-dialog.html',
                    controller: 'RegistrationDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                registration: ['Registration', '$stateParams', function(Registration, $stateParams) {
                    return Registration.get({id : $stateParams.registrationId}).$promise;
                }],
                workplaces: ['Workplace', 'CongressSelector', function(Workplace, CongressSelector) {
                    return Workplace.queryByCongress({ id: CongressSelector.getSelectedCongress().id });
                }],
                countries: ['Country', function(Country) {
                    return Country.query();
                }]
            }
        })
        .state('registration.delete', {
            parent: 'registration',
            url: '/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'RegIdStorage', function($stateParams, $state, $uibModal, RegIdStorage) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration/registration-delete-dialog.html',
                    controller: 'RegistrationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Registration', function(Registration) {
                            return Registration.get({id : $stateParams.registrationId}).$promise;
                        }]
                    }
                }).result.then(function() {
                    RegIdStorage.setLastRegId(null);
                    $state.transitionTo('registration', null, {
                        reload: true, inherit: false, notify: true
                    });
                    //$state.go('registration', null, { reload: 'registration' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('registration.upload', {
            parent: 'registration',
            url: '/upload',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/registration/registration-upload-dialog.html',
                    controller: 'RegistrationUploadDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                uploadFile: ['CongressSelector', function(CongressSelector) {
                    return {file: null, fileContentType: null, name: null, congressId: CongressSelector.getSelectedCongress().id };
                }]
            }
        })
        .state('registration.search', {
            parent: 'registration',
            url: '/search',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'RegIdStorage', 'registrationArray', function($stateParams, $state, $uibModal, RegIdStorage, registrationArray) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration/registration-search-dialog.html',
                    controller: 'RegistrationSearchController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                        registrationArray: function () {
                            return registrationArray;
                        }
                    }
                }).result.then(function (result) {
                    // $state.go('registration', {registrationId: result});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('registration.new.workplace-new', {
            parent: 'registration.new',
            url: '/workplace/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'workplaces', 'CongressSelector', function($stateParams, $state, $uibModal, registration, workplaces, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/workplace/workplace-dialog.html',
                    controller: 'WorkplaceDialogController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                vatRegNumber: null,
                                department: null,
                                zipCode: null,
                                city: null,
                                street: null,
                                phone: null,
                                fax: null,
                                email: null,
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function(result) {
                    registration.workplace = result;
                    registration.country = registration.workplace.country;
                    registration.zipCode = registration.workplace.zipCode;
                    registration.city = registration.workplace.city;
                    registration.street = registration.workplace.street;
                    registration.phone = registration.workplace.phone;
                    registration.email = registration.workplace.email;
                    registration.fax = registration.workplace.fax;

                    workplaces.push(result);
                        $state.go('registration.new');
                    }, function() {
                        $state.go('registration.new');
                    })
            }]
        })
        .state('registration.edit.workplace-new', {
            parent: 'registration.edit',
            url: '/workplace/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'workplaces', 'CongressSelector', function($stateParams, $state, $uibModal, registration, workplaces, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/workplace/workplace-dialog.html',
                    controller: 'WorkplaceDialogController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                vatRegNumber: null,
                                department: null,
                                zipCode: null,
                                city: null,
                                street: null,
                                phone: null,
                                fax: null,
                                email: null,
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function(result) {
                    registration.workplace = result;
                    registration.country = registration.workplace.country;
                    registration.zipCode = registration.workplace.zipCode;
                    registration.city = registration.workplace.city;
                    registration.street = registration.workplace.street;
                    registration.phone = registration.workplace.phone;
                    registration.email = registration.workplace.email;
                    registration.fax = registration.workplace.fax;

                    workplaces.push(result);
                        $state.go('registration.edit');
                    }, function() {
                        $state.go('registration.edit');
                    })
            }]
        })
        .state('registration.new.country-new', {
            parent: 'registration.new',
            url: '/country/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'countries', function($stateParams, $state, $uibModal, registration, countries) {
                $uibModal.open({
                    templateUrl: 'app/entities/country/country-dialog.html',
                    controller: 'CountryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function(result) {
                    registration.country = result;
                    countries.push(result);
                    $state.go('registration.new');
                }, function() {
                    $state.go('registration.new');
                });
            }]
        })
        .state('registration.edit.country-new', {
            parent: 'registration.edit',
            url: '/country/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'countries', function($stateParams, $state, $uibModal, registration, countries) {
                $uibModal.open({
                    templateUrl: 'app/entities/country/country-dialog.html',
                    controller: 'CountryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function(result) {
                    registration.country = result;
                    countries.push(result);
                    $state.go('registration.edit');
                }, function() {
                    $state.go('registration.edit');
                });
            }]
        })
        .state('registration.summary', {
            parent: 'registration',
            url: '/summary',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', function($stateParams, $state, $uibModal, registration) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration/registration-summary-dialog.html',
                    controller: 'RegistrationSummaryDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        summary: ['Registration', 'CongressSelector', function(Registration, CongressSelector) {
                            return Registration.getSummary({id : CongressSelector.getSelectedCongress().id});
                        }]
                    }
                }).result.then(function(result) {
                        $state.go('registration', {registrationId: registration.id});
                    }, function() {
                        $state.go('registration', {registrationId: registration.id});
                    })
            }]
        });
    }

    function stateChangeStart($rootScope, $state, Registration, CongressSelector, RegIdStorage) {
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            if (toState.name === 'registration' && !toParams.registrationId) {
                event.preventDefault();
                if (RegIdStorage.getLastRegId()) {
                    $state.go('registration', {registrationId: RegIdStorage.getLastRegId()});
                }
                else {
                    Registration.getDefault({id: CongressSelector.getSelectedCongress().id}, function (result) {
                            RegIdStorage.setLastRegId(result.id);
                            $state.go('registration', {registrationId: result.id});
                        },
                        function (result) {
                            $state.go('registration', {registrationId: 0});
                        });
                }
            }
        });
    }

})();
