package org.openmrs.module.sync2.web.controller;

import com.google.gson.Gson;
import org.openmrs.Patient;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
//import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static org.openmrs.module.sync2.api.utils.SyncUtils.createDefaultGson;

@Controller
public class ConflictResolutionController {

	@Autowired
	MergeConflictService mergeConflictService;

	private final SimpleObjectMessageConverter simpleConverter = new SimpleObjectMessageConverter();

	@RequestMapping(value = "/module/sync2/conflictResolution", method = RequestMethod.GET)
	public void initConflictResolution(
			ModelMap model,
			@RequestParam("conflictId") String conflictUuid
	) {
		model.addAttribute("conflictId", conflictUuid);

		MergeConflict mergeConflict = mergeConflictService.getByUuid(conflictUuid);

		String localObjJson = "{\n"
				+ "      \"resourceType\": \"Patient\",\n"
				+ "      \"id\": \"971b278d-b75d-492f-82dc-8a0d7725bd8f\",\n"
				+ "      \"extension\": [\n"
				+ "        {\n"
				+ "          \"url\": \"http://fhir-es.transcendinsights.com/stu3/StructureDefinition/resource-date-created\",\n"
				+ "          \"valueDateTime\": \"2018-11-20T12:26:35+00:00\"\n"
				+ "        }\n"
				+ "      ],\n"
				+ "      \"identifier\": [\n"
				+ "        {\n"
				+ "          \"use\": \"usual\",\n"
				+ "          \"system\": \"OpenMRS ID\",\n"
				+ "          \"value\": \"PAR110000G\"\n"
				+ "        }\n"
				+ "      ],\n"
				+ "      \"active\": true,\n"
				+ "      \"name\": [\n"
				+ "        {\n"
				+ "          \"use\": \"usual\",\n"
				+ "          \"family\": \"a\",\n"
				+ "          \"given\": [\n"
				+ "            \"b\"\n"
				+ "          ]\n"
				+ "        }\n"
				+ "      ],\n"
				+ "      \"gender\": \"male\",\n"
				+ "      \"birthDate\": \"2016-01-01\",\n"
				+ "      \"deceasedBoolean\": false,\n"
				+ "      \"address\": [\n"
				+ "        {\n"
				+ "          \"use\": \"home\",\n"
				+ "          \"line\": [\n"
				+ "            \"b\"\n"
				+ "          ]\n"
				+ "        }\n"
				+ "      ]\n"
				+ "    }";
		model.addAttribute("localObjJson", localObjJson);

		String foreignObjJson = "{\n"
				+ "  \"resourceType\": \"Patient\",\n"
				+ "  \"id\": \"01b9bf07-c746-4e33-8b3e-dfa2cb6d9834\",\n"
				+ "  \"extension\": [\n"
				+ "    {\n"
				+ "      \"url\": \"http://fhir-es.transcendinsights.com/stu3/StructureDefinition/resource-date-created\",\n"
				+ "      \"valueDateTime\": \"2018-11-15T10:26:01+00:00\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"identifier\": [\n"
				+ "    {\n"
				+ "      \"use\": \"usual\",\n"
				+ "      \"system\": \"OpenMRS ID\",\n"
				+ "      \"value\": \"CH210001A\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"active\": true,\n"
				+ "  \"name\": [\n"
				+ "    {\n"
				+ "      \"use\": \"usual\",\n"
				+ "      \"family\": \"f\",\n"
				+ "      \"given\": [\n"
				+ "        \"patient1125\"\n"
				+ "      ]\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"gender\": \"male\",\n"
				+ "  \"birthDate\": \"2017-01-01\",\n"
				+ "  \"deceasedBoolean\": false,\n"
				+ "  \"address\": [\n"
				+ "    {\n"
				+ "      \"use\": \"home\",\n"
				+ "      \"line\": [\n"
				+ "        \"a\"\n"
				+ "      ]\n"
				+ "    }\n"
				+ "  ]\n"
				+ "}";
		model.addAttribute("foreignObjJson", foreignObjJson);

		String className = mergeConflict.getFullClassName();
		try {
			Class<?> clazz = Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		model.addAttribute("className", className);
	}
}
