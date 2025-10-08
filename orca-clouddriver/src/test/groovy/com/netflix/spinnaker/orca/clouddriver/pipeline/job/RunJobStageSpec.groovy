package com.netflix.spinnaker.orca.clouddriver.pipeline.job

import com.netflix.spinnaker.orca.clouddriver.tasks.job.DestroyJobTask
import com.netflix.spinnaker.orca.pipeline.graph.StageGraphBuilderImpl
import com.netflix.spinnaker.orca.pipeline.model.PipelineExecutionImpl
import com.netflix.spinnaker.orca.pipeline.model.StageExecutionImpl
import spock.lang.Specification
import spock.lang.Subject

import static com.netflix.spinnaker.orca.api.pipeline.models.ExecutionType.PIPELINE

class RunJobStageSpec extends Specification {
  def destroyJobTask = Mock(DestroyJobTask)

  @Subject
  def runJobStage = new RunJobStage(destroyJobTask, null)

  def "should suppress output after stage if suppression enabled"() {
    given:
    def stage = new StageExecutionImpl(new PipelineExecutionImpl(PIPELINE, "testapp"), "runJobManifest", stageContext)
    stage.setOutputs([foo: "bar"])
    def graph = StageGraphBuilderImpl.afterStages(stage)

    when:
    runJobStage.afterStages(stage, graph)

    then:
    assert stage.getOutputs() == expectedOutputs

    where:
    stageContext            || expectedOutputs
    [:]                     || [foo: "bar"]
    [suppressOutput: false] || [foo: "bar"]
    [suppressOutput: true]  || [:]
  }

  def "should suppress after failed stage output if suppression enabled"() {
    given:
    def stage = new StageExecutionImpl(new PipelineExecutionImpl(PIPELINE, "testapp"), "runJobManifest", stageContext)
    stage.setOutputs([foo: "bar"])
    def graph = StageGraphBuilderImpl.afterStages(stage)

    when:
    runJobStage.onFailureStages(stage, graph)

    then:
    assert stage.getOutputs() == expectedOutputs

    where:
    stageContext            || expectedOutputs
    [:]                     || [foo: "bar"]
    [suppressOutput: false] || [foo: "bar"]
    [suppressOutput: true]  || [:]
  }
}
