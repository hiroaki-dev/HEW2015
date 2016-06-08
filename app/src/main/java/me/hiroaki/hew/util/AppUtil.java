package me.hiroaki.hew.util;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.realm.Realm;
import io.realm.RealmObject;
import me.hiroaki.hew.api.HewApi;
import me.hiroaki.hew.model.EventResponse;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.Category;
import me.hiroaki.hew.model.RealmObject.Event;
import me.hiroaki.hew.model.RealmObject.EventCategory;
import me.hiroaki.hew.model.LoginInfomation;
import me.hiroaki.hew.model.RealmObject.Questionnaire;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hiroaki on 2016/02/14.
 */
public class AppUtil {
	public static final String HOST_URL = "https://hiroaki.me/";

	public static Retrofit getRetrofitBuilder() {
		Gson gson = new GsonBuilder()
				.setExclusionStrategies(new ExclusionStrategy() {
					@Override
					public boolean shouldSkipField(FieldAttributes f) {
						return f.getDeclaringClass().equals(RealmObject.class);
					}

					@Override
					public boolean shouldSkipClass(Class<?> clazz) {
						return false;
					}
				})
				.registerTypeAdapter(Event.class, new EventSerializer())
				.registerTypeAdapter(Category.class, new CategorySerializer())
				.registerTypeAdapter(EventCategory.class, new EventCategorySerializer())
				.registerTypeAdapter(Questionnaire.class, new QuestionnaireSerializer())
				.registerTypeAdapter(Booth.class, new BoothSerializer())
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();


		return new Retrofit.Builder()
				.baseUrl(AppUtil.HOST_URL)
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();
	}

	public static HewApi getHewApiInstance() {
		return getRetrofitBuilder().create(HewApi.class);
	}

	public static class EventSerializer implements JsonSerializer<Event> {

		@Override
		public JsonElement serialize(Event src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.getId());
			jsonObject.addProperty("name", src.getName());
			jsonObject.add("start", context.serialize(src.getStart()));
			jsonObject.add("end", context.serialize(src.getEnd()));
			jsonObject.addProperty("detail", src.getDetail());
			jsonObject.add("category", context.serialize(src.getCategory()));
			return jsonObject;
		}
	}

	public static class CategorySerializer implements JsonSerializer<Category> {

		@Override
		public JsonElement serialize(Category src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.getId());
			jsonObject.add("event_category", context.serialize(src.getEventCategory()));
			jsonObject.add("booths", context.serialize(src.getBooths()));
			return jsonObject;
		}
	}

	public static class EventCategorySerializer implements JsonSerializer<EventCategory> {

		@Override
		public JsonElement serialize(EventCategory src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.getId());
			jsonObject.addProperty("name", src.getName());
			jsonObject.addProperty("opinion_flag", src.isOpinionFlag());
			jsonObject.addProperty("good_flag", src.isGoodFlag());
			jsonObject.addProperty("question_flag", src.isQuestionFlag());
			jsonObject.add("questionnaire", context.serialize(src.getQuestionnaires()));
			return jsonObject;
		}
	}

	public static class QuestionnaireSerializer implements JsonSerializer<Questionnaire> {

		@Override
		public JsonElement serialize(Questionnaire src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.getId());
			jsonObject.addProperty("event_category_id", src.getEventCategoryId());
			jsonObject.addProperty("line_num", src.getLineNum());
			jsonObject.addProperty("content", src.getContent());
			jsonObject.addProperty("choice1", src.getChoice1());
			jsonObject.addProperty("choice2", src.getChoice2());
			jsonObject.addProperty("choice3", src.getChoice3());
			jsonObject.addProperty("choice4", src.getChoice4());
			jsonObject.addProperty("choice5", src.getChoice5());
			return jsonObject;
		}
	}

	public static class BoothSerializer implements JsonSerializer<Booth> {

		@Override
		public JsonElement serialize(Booth src, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.getId());
			jsonObject.addProperty("name", src.getName());
			jsonObject.addProperty("description", src.getDescription());
			jsonObject.addProperty("representative", src.getRepresentative());
			jsonObject.addProperty("good", src.getGood());
			return jsonObject;
		}
	}

	public static String getEventImageUrl(int id) {
		return HOST_URL + "hew/web/upload/event/" + id + ".png";
	}

	public static String getBoothImageUrl(String id) {
		return HOST_URL + "hew/web/upload/booth/" + id + ".png";
	}

	public static String[] getSplitedString(String description) {
		return description.split(",", 0);
	}



	public static void getEvents(final Context context) {
		LoginSetting loginSetting = new LoginSetting(context);
		Call<EventResponse> response = AppUtil.getHewApiInstance()
				.getEvents(loginSetting.getLoginId());
		response.enqueue(new Callback<EventResponse>() {
			@Override
			public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
				if (!response.isSuccess()) {
					Toast.makeText(context, "更新できませんでした" + response.message() + response.code(), Toast.LENGTH_LONG).show();
					return;
				}

				if (response.code() < 300) {
					Realm realm = Realm.getInstance(context);
					realm.beginTransaction();
					realm.copyToRealmOrUpdate(response.body().getEvent());
					realm.commitTransaction();
				} else {
					Toast.makeText(context, "更新できませんでした", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<EventResponse> call, Throwable t) {
				Toast.makeText(context, "更新できませんでした", Toast.LENGTH_LONG).show();
			}
		});
	}


}
