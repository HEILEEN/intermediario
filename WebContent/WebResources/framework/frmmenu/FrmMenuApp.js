var FrmMainApp=angular.module('FrmMainApp', ['ui.tree', 'ngGrid', 'ngRoute', 'ui.bootstrap', 'ngRoute', 'ngFileUpload' ]);  

FrmMainApp.config(['$routeProvider',
   	function($routeProvider) {
   	  $routeProvider.
   	    when('/FrmPerfil', {
   	      templateUrl: function(params) {
   	          return  WEB_SERVER+'FrmPerfil/';
   	      },
   	      controller: 'FrmPerfilController'
   	    }).
   	    when('/Soporte/:soporteId', {
  	      templateUrl: function(params) {
  	          return  WEB_SERVER+'FrmConsulta/Soporte/';
  	      },
  	      controller: 'SoporteController'
  	    }).	  
  	    when('/FrmTransaccion', {
   	      templateUrl: function(params) {
   	    	  console.log(WEB_SERVER+'FrmTransaccion/');
   	          return  WEB_SERVER+'FrmTransaccion/';
   	      },
   	      controller: 'FrmTransaccionController'
   	    }).
   	    otherwise({
   	      
   	    });
}]);