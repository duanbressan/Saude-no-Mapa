package com.mat_brandao.saudeapp.view.group;

import android.content.Context;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.CreateGroup;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.model.MembroGrupo;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class GroupInteractorImpl implements GroupInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final User mUser;

    public GroupInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
    }

    @Override
    public Observable<Response<List<Grupo>>> requestGroup(String nomeFantasia) {
        return RestClient.get()
                .getGroups(mContext.getString(R.string.app_id), nomeFantasia, null);
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateGroup(String nomeFantasia) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .createGroup(new CreateGroup(Integer.valueOf(mContext.getString(R.string.app_id)),
                        nomeFantasia));
    }

    @Override
    public Observable<Response<List<MembroGrupo>>> requestGroupMembers(int groupId) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getGroupMembers(groupId);
    }

    @Override
    public Observable<Response<User>> requestUser(Long userId) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getUser(userId);
    }

    @Override
    public Observable<Response<ResponseBody>> requestLeaveGroup(Integer codGrupo, Long membroId) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .leaveGroup(codGrupo, membroId);
    }

    @Override
    public Observable<Response<ResponseBody>> requestJoinGroup(Integer codGrupo) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .joinGroup(codGrupo, mUser.getId());
    }

    @Override
    public User getUser() {
        return mUserRepository.getUser();
    }

    @Override
    public void setGroupMemberIds(List<MembroGrupo> mGroupMembers, Integer groupId) {
        for (MembroGrupo mGroupMember : mGroupMembers) {
            mGroupMember.setMembroId(GenericUtil
                    .getContentIdFromUrl(String.valueOf(groupId),
                            mGroupMember.getLinks().get(0).getHref()));
            mGroupMember.setUsuarioId(GenericUtil
                    .getNumbersFromString(mGroupMember.getLinks().get(1).getHref()));
        }
    }

    @Override
    public boolean isUserJoined(List<MembroGrupo> mGroupMembers) {
        for (MembroGrupo mGroupMember : mGroupMembers) {
            if (mGroupMember.getUsuarioId() == mUser.getId()) {
                return true;
            }
        }
        return false;
    }
}