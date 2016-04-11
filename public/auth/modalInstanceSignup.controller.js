angular
.module('farmers.module')
.controller('ModalSignupInstanceCtrl', function ($scope, $uibModalInstance, AuthService,$location) {

  $scope.createUser = function (user) {
    AuthService.createUser(user)
    .success(function(res){
      console.log("CREATED",res);

      if(res.userType === 'Farmer') {
        $location.path("/farmers/"+ res.id);
      } else if (res.userType === 'Buyer') {
        $location.path("/buyers/" + res.id);
      }
    })

    .error(function(err) {
      console.log("SHIT", err);
    });
    $uibModalInstance.close();
  };
  // $scope.createBuyer = function (user) {
  //   HomeService.createBuyer(user)
  //   $uibModalInstance.close($scope.selected.item);
  // };


  $scope.cancel = function () {
    $uibModalInstance.close('cancel');
  };
});
 // $scope.loginUser = function (user) {
 //   console.log("TEST MODAL", user);
 //   AuthService.loginUser(user)
 //   .success(function(res){
 //     console.log(res);
 //     if(res.userType === 'Farmer') {
 //       $location.path("/farmers/"+ res.id);
 //     } else if (res.userType === 'Buyer') {
 //       $location.path("/buyers/" + res.id);
 //     }
 //   })
 //   .error(function(err) {
 //     console.log("SHIT", err);
 //   });
 //   $uibModalInstance.close();
 // };
 // $scope.createBuyer = function (user) {
 //   HomeService.createBuyer(user)
 //   $uibModalInstance.close($scope.selected.item);
 // };


//  $scope.cancel = function () {
//    $uibModalInstance.close('cancel');
//  };
// });
