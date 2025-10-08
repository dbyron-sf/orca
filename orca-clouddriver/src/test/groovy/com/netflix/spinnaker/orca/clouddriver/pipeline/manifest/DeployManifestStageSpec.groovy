package com.netflix.spinnaker.orca.clouddriver.pipeline.manifest


import com.netflix.spinnaker.orca.pipeline.graph.StageGraphBuilderImpl
import com.netflix.spinnaker.orca.pipeline.model.PipelineExecutionImpl
import com.netflix.spinnaker.orca.pipeline.model.StageExecutionImpl
import spock.lang.Specification
import spock.lang.Subject

import static com.netflix.spinnaker.orca.api.pipeline.models.ExecutionType.PIPELINE

class DeployManifestStageSpec extends Specification {

  @Subject
  def deployManifestStage = new DeployManifestStage()

  def "should suppress output after stage if suppression enabled"() {
    given:
    def stage = new StageExecutionImpl(new PipelineExecutionImpl(PIPELINE, "testapp"), "deployManifest", stageContext)
    stage.setOutputs([foo: "bar"])
    def graph = StageGraphBuilderImpl.afterStages(stage)

    when:
    deployManifestStage.afterStages(stage, graph)

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
    def stage = new StageExecutionImpl(new PipelineExecutionImpl(PIPELINE, "testapp"), "deployManifest", stageContext)
    stage.setOutputs([foo: "bar"])
    def graph = StageGraphBuilderImpl.afterStages(stage)

    when:
    deployManifestStage.onFailureStages(stage, graph)

    then:
    assert stage.getOutputs() == expectedOutputs

    where:
    stageContext            || expectedOutputs
    [:]                     || [foo: "bar"]
    [suppressOutput: false] || [foo: "bar"]
    [suppressOutput: true]  || [:]
  }
}
