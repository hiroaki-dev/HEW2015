package me.hiroaki.hew.api;

import java.util.List;

import me.hiroaki.hew.model.RealmObject.Answer;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.EventResponse;
import me.hiroaki.hew.model.Response;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hiroaki on 2016/02/14.
 */
public interface HewApi {
	String GET_EVENTS = "/hew/api/get_events.php";
	String GET_BOOTHS = "/hew/api/get_booths.php";
	String POST_LOGIN = "/hew/api/check_login.php";
	String POST_GOOD = "/hew/api/post_good.php";
	String POST_QUESTION = "/hew/api/post_question.php";
	String POST_OPINION = "/hew/api/post_opinion.php";
	String POST_FEEDBACK = "/hew/api/post_feedback.php";

	@GET(GET_EVENTS)
	Call<EventResponse> getEvents (
		@Query("student_id") String studentId
	);

	@GET(GET_BOOTHS)
	Call<List<Booth>> getBooths (
		@Query("event_id") int eventId
	);

	@FormUrlEncoded
	@POST(POST_LOGIN)
	Call<Response> postLogin (
		@Field("student_id") String studentId,
		@Field("password") String password
	);

	@FormUrlEncoded
	@POST(POST_GOOD)
	Call<Response> postGood (
		@Field("booth_id") String boothId,
		@Field("add_flag") boolean addFlag
	);

	@FormUrlEncoded
	@POST(POST_QUESTION)
	Call<Response> postAnswers(
			@Field("booth_id") String boothId,
			@Field("student_id") String studentId,
			@Field("answers[]") List<Answer> answers
	);

	@FormUrlEncoded
	@POST(POST_OPINION)
	Call<Response> postOpinion (
			@Field("booth_id") String boothId,
			@Field("student_id") String studentId,
			@Field("content") String content
	);

	@FormUrlEncoded
	@POST(POST_FEEDBACK)
	Call<Response> postFeedback (
			@Field("booth_id") String boothId,
			@Field("student_id") String studentId,
			@Field("content") String content,
			@Field("answers[]") List<Answer> answers
	);
}
