package com.elder.zcommonmodule.Service

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse


class HttpInteface {

    //网络请求接口类


    interface startDriverResult {
        fun startDriverSuccess(it: String)
        fun startDriverError(error: Throwable)
    }

    interface getMsgNotifyCount {
        fun getNotifyCountSuccess(it: String)
        fun getNotifyCountError(ex: Throwable)
    }

    interface queryCommandMeList {
        fun CommandSuccess(it: String)
        fun CommandError(ex: Throwable)
    }

    interface queryAtmeList {
        fun AtmeListSuccess(it: String)
        fun AtmeListError(ex: Throwable)
    }

    interface IUploadDriverFiles {
        fun UploadDriverSuccess(response: String)
        fun UploadDriverError(ex: Throwable)
    }

    interface IUploadDriverImages {
        fun UploadDriverImagesSuccess(response: String)
        fun UploadDriverImagesError(ex: Throwable)
    }

    interface querySystemNotifyList {
        fun SystemNotifyListSuccess(response: String)
        fun SystemNotifyListError(ex: Throwable)
    }

    interface queryActiveNotifyList {
        fun ActiveNotifyListSuccess(response: String)
        fun ActiveNotifyListError(ex: Throwable)
    }

    interface deleteSystemNotifyList {
        fun SystemNotifyDeleteListSuccess(response: String)
        fun SystemNotifyDeleteListError(ex: Throwable)
    }

    interface deleteActiveNotifyList {
        fun ActiveNotifyListDeleteSuccess(response: String)
        fun ActiveNotifyListDeleteError(ex: Throwable)
    }

    interface LoginResult {
        fun LoginSuccess(response: BaseResponse)
        fun LoginError(error: Throwable)
    }


    interface CreateTeamResult {
        fun CreateTeamSuccess(it: String)
        fun CreateTeamError(ex: Throwable)
    }

    interface JoinTeamResult {
        fun JoinTeamSuccess(it: String)
        fun JoinTeamError(ex: Throwable)
    }

    interface CheckTeamStatus {
        fun CheckTeamStatusSucccess(it: BaseResponse)
        fun CheckTeamStatusError(ex: Throwable)
    }

    interface QueryRollInfo {
        fun QueryRollInfoSuccess(it: String)
        fun QueryRollInfoError(ex: Throwable)
    }

    interface getMakerList {
        fun getMakerListSuccess(it: String)
        fun getMakerListError(ex: Throwable)
    }

    interface getRoadBookList {
        fun getRoadBookSuccess(it: String)
        fun getRoadBookError(ex: Throwable)
    }

    interface RoadBookDetail {
        fun getRoadBookDetailSuccess(it: String)
        fun getRoadBookDetailError(ex: Throwable)

    }

    interface SearchRoadBook {
        fun SearchRoadBookSuccess(it: String)
        fun SearchRoadBookError(ex: Throwable)
    }


    interface DownLoadRoodBook {
        fun DownLoadRoadBookSuccess(it: BaseResponse)
        fun DownLoadRoadBookError(ex: Throwable)
    }

    interface getMyRoadBook {
        fun getMyRoadBookSuccess(it: String)
        fun getMyRoadBookkError(ex: Throwable)
    }

    interface SocialUploadPhoto {
        fun postPhotoSuccess(it: String)
        fun postPhotoError(ex: Throwable)
    }


    interface SocialDynamicsList {
        fun ResultSDListSuccess(it: String)
        fun ResultSDListError(ex: Throwable)
    }


    interface SocialReleaseDynamics {

        fun ResultReleaseDynamicsSuccess(it: String)
        fun ResultReleaseDynamicsError(ex: Throwable)

    }

    interface SocialDynamicsCollection {
        fun ResultCollectionSuccess(it: String)
        fun ResultCollectionError(ex: Throwable)
    }

    interface SocialDynamicsComment {
        fun ResultCommentSuccess(it: String)
        fun ResultCommentError(ex: Throwable)
    }

    interface SocialDynamicsCommentList {
        fun ResultCommentListSuccess(it: String)
        fun ResultCommentListError(ex: Throwable)
    }

    interface SocialDynamicsLike {
        fun ResultLikeSuccess(it: String)
        fun ResultLikeError(ex: Throwable)
    }

    interface SocialCommentLike {
        fun ResultCommentLikeSuccess(it: String)
        fun ResultCommentLikeError(ex: Throwable)
    }

    interface SocialDynamicsFocus {
        fun ResultFocusSuccess(it: String)
        fun ResultFocusError(ex: Throwable)
    }


    interface SocialDynamicsFocuserList {
        fun ResultFocuserSuccess(it: String)
        fun ResultFocuserError(ex: Throwable)
    }


    interface SocialDynamicsLikerList {
        fun ResultLikerSuccess(it: String)
        fun ResultLikerError(ex: Throwable)
    }

    //个人
    interface SocialMyDynamicList {
        fun ResultSocialMyDynamicSuccess(it: String)
        fun ResultSocialMyDynamicError(ex: Throwable)
    }

    interface PrivateRestoreList {
        fun ResultPrivateDynamicSuccess(it: String)
        fun ResultPrivateDynamicError(ex: Throwable)
    }

    interface PrivateLikeList {
        fun ResultPrivateLikeSuccess(it: String)
        fun ResultPrivateLikeError(ex: Throwable)
    }


    interface ExitLogin {
        fun ExitLoginSuccess(it: String)
        fun ExitLoginError(ex: Throwable)
    }

    interface PrivateFansList {
        fun ResultPrivateFansSuccess(it: String)
        fun ResultPrivateFansError(ex: Throwable)
    }

    interface PrivateFocusList {
        fun ResultPrivateFocusSuccess(it: String)
        fun ResultPrivateFocusError(ex: Throwable)
    }


    interface HomeResult {
        fun ResultHomeSuccess(it: String)

        fun ResultHomeError(it: Throwable)
    }

    interface getRankResult {
        fun ResultRankingSuccess(it: String)
        fun ResultRankingError(it: Throwable)
    }


    interface CanalierResult {
        fun ResultCanalierSuccess(it: String)
        fun ResultCanalierError(it: Throwable)
    }

    interface SearchMember {
        fun SearchSuccess(it: String)

        fun SearchError(it: Throwable)
    }

    interface GetLikeResult {
        fun GetLikeSuccess(it: String)
        fun getLikeError(it: Throwable)
    }


    interface deleteSocialResult {
        fun deleteSocialSuccess(it: String)
        fun deleteSocialError(it: Throwable)
    }

    interface PartyHome {
        fun getPartyHomeSuccess(it: String)
        fun getPartyHomeError(it: Throwable)
    }

    interface PartyDetail {
        fun getPartyDetailSuccess(it: String)
        fun getPartyDetailError(it: Throwable)
    }

    interface PartyOrganization {
        fun getPartyOrganizationSuccess(it: String)
        fun getPartyOrganizationError(it: Throwable)
    }

    interface PartySign {
        fun getPartySignSuccess(it: String)
        fun getPartySignError(it: Throwable)
    }

    interface PartySearch {
        fun getPartySearchSuccess(it: String)
        fun getPartySearchError(it: Throwable)
    }

    interface PartySuject_inf {
        fun PartySubjectSucccess(it: String)
        fun PartySubjectError(it: Throwable)
    }

    interface PartyMoto_inf {
        fun PartyMotoSucccess(it: String)
        fun PartyMotoError(it: Throwable)
    }

    interface PartyClock_inf {
        fun PartyClockSucccess(it: String)
        fun PartyClockError(it: Throwable)
    }

    interface PartyAlbum_inf {
        fun PartyAlbumSucccess(it: String)
        fun PartyAlbumError(it: Throwable)
    }

    interface PartyRanking_inf {
        fun PartyRankingSucccess(it: String)
        fun PartyRankingError(it: Throwable)
    }

    interface PartyRestore_inf {
        fun PartyRestoreSucccess(it: String)
        fun PartyRestoreError(it: Throwable)
    }

    interface PartyUnReadNotify_inf {
        fun PartyUnReadNotifySucccess(it: String)
        fun PartyUnReadNotifyError(it: Throwable)
    }

    interface IRelogin {
        fun IReloginSuccess(t: String)
        fun IReloginError()
    }
}