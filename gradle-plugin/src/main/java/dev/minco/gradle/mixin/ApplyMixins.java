package dev.minco.gradle.mixin;

import java.io.File;

import lombok.NonNull;
import lombok.val;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.minco.mixin.internal.ApplicationType;
import dev.minco.mixin.internal.MixinApplicator;

public abstract class ApplyMixins {
	private static final Logger logger = LoggerFactory.getLogger(ApplyMixins.class);

	@Input
	@NonNull
	public abstract Property<ApplicationType> getApplicationType();

	@Classpath
	@InputFiles
	@NonNull
	public abstract ConfigurableFileCollection getMixinSource();

	/*
	removed this cache in case it's causing trouble
	@Internal
	private transient MixinApplicator applicator;
	 */

	public MixinApplicator makeApplicator() {
		/*if (applicator != null) {
			return applicator;
		}*/
		val applicator = new MixinApplicator();
		applicator.setNoMixinIsError(true);
		applicator.setNotAppliedIsError(true);
		if (logger != null) {
			applicator.setLog(logger::info);
		}
		for (File file : getMixinSource().getFiles()) {
			applicator.addSource(file.toPath());
		}
		applicator.setApplicationType(getApplicationType().get());
		//this.applicator = applicator;
		return applicator;
	}

	public void transformArtifact(File input, File output) {
		logger.info("Transforming " + input + " to " + output);
		makeApplicator().getMixinTransformer().transform(input.toPath(), output.toPath());
	}

}
