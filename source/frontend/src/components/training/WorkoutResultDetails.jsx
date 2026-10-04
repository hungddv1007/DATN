import './WorkoutResultDetails.css';

const getExerciseMetrics = (exercise) => [
  exercise.setCount && `${exercise.setCount} hiệp`,
  exercise.repCount && `${exercise.repCount} lần`,
  exercise.weightKg != null && `${exercise.weightKg} kg`,
  exercise.durationMinutes && `${exercise.durationMinutes} phút`,
].filter(Boolean);

const WorkoutResultDetails = ({ session, compact = false }) => {
  const exercises = session?.exercises || [];
  const hasResult = exercises.length > 0 || Boolean(session?.actualNote);

  if (!hasResult) {
    return <div className="workout-result-empty">Buổi tập chưa có kết quả chi tiết.</div>;
  }

  return (
    <div className={`workout-result-details${compact ? ' is-compact' : ''}`}>
      {exercises.length > 0 && (
        <section className="workout-result-section">
          <h4>Bài tập đã thực hiện</h4>
          <div className="workout-result-exercises">
            {exercises.map((exercise, index) => {
              const metrics = getExerciseMetrics(exercise);
              return (
                <article
                  className="workout-result-exercise"
                  key={exercise.id || `${exercise.exerciseId}-${index}`}
                >
                  <div className="workout-result-exercise-heading">
                    <strong>{exercise.exerciseName}</strong>
                    <span>{metrics.join(' · ') || 'Đã thực hiện'}</span>
                  </div>
                  {exercise.note && (
                    <p>
                      <b>Ghi chú kỹ thuật/kết quả:</b> {exercise.note}
                    </p>
                  )}
                </article>
              );
            })}
          </div>
        </section>
      )}

      {session?.actualNote && (
        <section className="workout-result-overall-note">
          <h4>Nhận xét của PT sau buổi tập</h4>
          <p>{session.actualNote}</p>
        </section>
      )}
    </div>
  );
};

export default WorkoutResultDetails;
