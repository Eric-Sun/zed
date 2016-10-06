package com.j13.zed.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.helper.FileIconHelper;
import com.j13.zed.user.User;
import com.j13.zed.user.UserContext;
import com.j13.zed.util.Constants;
import com.j13.zed.util.DebugLog;
import com.j13.zed.util.NetworkUtils;
import com.j13.zed.view.crop.CropImageActivity;
import com.j13.zed.view.crop.ImagePathUtil;
import com.j13.zed.view.dialog.AlertDialog;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import de.greenrobot.event.EventBus;

public class UserInfoSettingActivity extends BaseActivity implements View.OnClickListener{

    private static final int ITEM_MALE = 0;
    private static final int ITEM_FEMALE = 1;

    private static final int ITEM_LOCAL = 0;
    private static final int ITEM_CAMERA = 1;

    private static final int REQ_SELECT_ALBUM = 3001;
    private static final int REQ_SELECT_CAMERA = 3002;
    private static final int REQ_CROP_PHOTO = 3003;

    private static final String TEMP_PHOTO = "tmp_user_photo.jpg";

    private ImageView mUserIcon;
    private TextView mUserName;
    private TextView mUserSex;
    private TextView mUserDesc;

    private String mCropPhotoPath;
    private FileIconHelper mIconHelper;

    private Long mUploadId;

    @Override
    protected int geContentViewId() {
        return R.layout.activity_user_info_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIconHelper = FileIconHelper.getInstance(this);
        EventBus.getDefault().register(this);

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initViews() {
        setupClick(R.id.layout_user_icon);
        setupClick(R.id.layout_user_name);
        setupClick(R.id.layout_user_sex);
        setupClick(R.id.layout_user_desc);

        mUserIcon = (ImageView) findViewById(R.id.user_icon_img);
        mUserName = (TextView) findViewById(R.id.tv_user_name);
        mUserSex = (TextView) findViewById(R.id.tv_user_sex);
        mUserDesc = (TextView) findViewById(R.id.tv_user_desc);

        User user = UserContext.getInstance(this).getCurrentUser();
        if (user != null) {
            setUserIcon(user.getHeadIconUrl());

            String name = user.getUserName();
            String phone = user.getPhoneNum();
            if (name.equals(phone) && name.length() == 11) {
                name = name.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
            setUserName(name);
            setUserSex(user.getSex());
            setUserDesc(user.getDesc());
        }
    }

    public void setUserIcon(String url) {
        if (TextUtils.isEmpty(url)) {
            mUserIcon.setImageResource(R.drawable.my_head_icon);
            return;
        }
        mIconHelper.loadInto(url, 0, 0, R.drawable.my_head_icon, mUserIcon, true);
    }

    public void setUserName(String name) {
        mUserName.setText(name);
    }

    public void setUserDesc(String desc) {
        mUserDesc.setText(desc);
    }

    public void setUserSex(String sex) {
        if ("male".equals(sex)) {
            mUserSex.setText(R.string.male);
        } else if ("female".equals(sex)) {
            mUserSex.setText(R.string.female);
        } else {
            mUserSex.setText("");
        }
    }

    private View setupClick(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_user_icon:
                showUploadAvatarDialog();
                break;
            case R.id.layout_user_name:
                showUserNameEdit();
                break;
            case R.id.layout_user_sex:
                showUserSexDialog();
                break;
            case R.id.layout_user_desc:
                showUserDescEdit();
                break;
            default:
                break;
        }
    }

    private void showUserNameEdit() {
        Intent intent = new Intent(this, UserNameEditActivity.class);
        intent.putExtra(Constants.EXTRA_USER_NAME, mUserName.getText());
        startActivity(intent);
    }

//    public void onEventMainThread(UpdateUserNameEvent event) {
//        dismissProgressDialog();
//
//        if (event == null || event.resultCode != UpdateUserNameEvent.RESULT_OK) {
//            showToast(R.string.update_failed);
//            return;
//        }
//
//        setUserName(event.name);
//    }

    private void showUserDescEdit() {
        Intent intent = new Intent(this, UserDescEditActivity.class);
        intent.putExtra(Constants.EXTRA_USER_DESC, mUserDesc.getText());
        startActivity(intent);
    }

//    public void onEventMainThread(UpdateUserDescEvent event) {
//        dismissProgressDialog();
//
//        if (event == null || event.resultCode != UpdateUserDescEvent.RESULT_OK) {
//            showToast(R.string.update_failed);
//            return;
//        }
//
//        setUserDesc(event.desc);
//    }

    private void showUploadAvatarDialog() {
        String[] items = getResources().getStringArray(R.array.array_upload_avatar);
        new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case ITEM_LOCAL:
                        selectLocalImage();
                        break;
                    case ITEM_CAMERA:
                        selectCameraPhoto();
                        break;
                    default:
                        break;
                }
            }
        }).create().show();
    }

    private void showUserSexDialog() {
        String[] items = getResources().getStringArray(R.array.array_user_sex);
        new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectSex(which);
            }
        }).create().show();
    }

    private void selectSex(int which) {
        String sex = "";
        switch (which) {
            case ITEM_MALE:
                sex = "male";
                break;
            case ITEM_FEMALE:
                sex = "female";
                break;
            default:
                break;
        }

        if (!NetworkUtils.hasInternet(this)) {
            showToast(R.string.network_not_available);
            return;
        }

        //更新用户性别
        showProgressDialog(null, getString(R.string.update_loading));
//        UserInfoManager.getInstance(this).updateUserSex(sex);
    }

//    public void onEventMainThread(UpdateUserSexEvent event) {
//        dismissProgressDialog();
//
//        if (event == null || event.resultCode != UpdateUserSexEvent.RESULT_OK) {
//            showToast(R.string.update_failed);
//            return;
//        }
//
//        setUserSex(event.sex);
//    }

    private Uri getTempPhotoUri() {
        File temp = new File(getExternalCacheDir(), TEMP_PHOTO);
        String uriString = "file://" + temp.getAbsolutePath();
        return Uri.parse(uriString);
    }

    private void selectLocalImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.upload_avatar_local)), REQ_SELECT_ALBUM);
    }

    private void selectCameraPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempPhotoUri());
        startActivityForResult(intent, REQ_SELECT_CAMERA);
    }

    private void doCropPhoto(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(this, CropImageActivity.class);
            intent.putExtra(CropImageActivity.EXTRA_IMAGE_URI, uri);
            startActivityForResult(intent, REQ_CROP_PHOTO);
        } else {
            DebugLog.w("UserInfoSetting", "crop image uri is null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_SELECT_ALBUM:
                onResultFromAlbum(data, resultCode);
                break;
            case REQ_SELECT_CAMERA:
                onResultFormCamera(data, resultCode);
                break;
            case REQ_CROP_PHOTO:
                onResultCropPhoto(data, resultCode);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void onResultFromAlbum(Intent data, int resultCode) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }
        Uri uri = data.getData();
        doCropPhoto(uri);
    }

    private void onResultFormCamera(Intent data, int resultCode) {
        if (resultCode != RESULT_OK) {
            return;
        }
        doCropPhoto(getTempPhotoUri());
    }

    private void onResultCropPhoto(Intent data, int resultCode) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }

        File temp = new File(getExternalCacheDir(), TEMP_PHOTO);
        if (temp.exists()) {
            temp.delete();
        }

        Uri uri = (Uri) data.getExtras().get(
                CropImageActivity.EXTRA_CROP_IMAGE_URI);
        mCropPhotoPath = ImagePathUtil.getPath(this, uri);

        if (!NetworkUtils.hasInternet(this)) {
            showToast(R.string.network_not_available);
            return;
        }

        //请求上传头像
        showProgressDialog(null, getString(R.string.upload_avatar_loading));
//        UserInfoManager.getInstance(this).requestUploadAvatar();
    }

//    public void onEventMainThread(GetAvatarTokenEvent event) {
//        if (event == null || TextUtils.isEmpty(event.key) || TextUtils.isEmpty(event.uploadToken)) {
//            dismissProgressDialog();
//            showToast(R.string.upload_avatar_failed);
//            return;
//        }
//
//        if (!NetworkUtils.hasInternet(this)) {
//            dismissProgressDialog();
//            showToast(R.string.network_not_available);
//            return;
//        }
//        //上传头像
//        mUploadId = System.currentTimeMillis();
//        Bundle extra = QiniuUploadManager.buildExtra(event.key, event.uploadToken, mUploadId);
//        mUploadManager.put(mCropPhotoPath, null, this, this, extra);
//    }

//
//    public void onEventMainThread(CommitAvatarEvent event) {
//        dismissProgressDialog();
//        if (event == null || TextUtils.isEmpty(event.avatarUrl)) {
//            showToast(R.string.upload_avatar_failed);
//            return;
//        }
//
//        RequestCreator creator = mIconHelper.getRequestCreator(Uri.fromFile(new File(mCropPhotoPath)),
//                0, 0, R.drawable.default_head_icon);
//        creator.transform(new CircleTransformation());
//        creator.into(mUserIcon);
//    }
}
