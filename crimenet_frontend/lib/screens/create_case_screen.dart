import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import '../services/auth_service.dart';

class CreateCaseScreen extends StatefulWidget {
  final VoidCallback? onCaseCreated;
  const CreateCaseScreen({super.key, this.onCaseCreated});

  @override
  State<CreateCaseScreen> createState() => _CreateCaseScreenState();
}

class _CreateCaseScreenState extends State<CreateCaseScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descController = TextEditingController();
  File? _imageFile;
  File? _mediaFile;
  bool _isLoading = false;

  Future<void> _pickImage() async {
    final picked = await ImagePicker().pickImage(source: ImageSource.gallery);

    if (picked != null) {
      print("Picked XFile: " + picked.toString() + " path: " + picked.path);
      setState(() {
        _imageFile = File(picked.path);
      });
    } else {
      print("No image picked");
    }
  }

  Future<void> _pickMedia() async {
    final picked = await ImagePicker().pickVideo(source: ImageSource.gallery);
    if (picked != null) {
      setState(() {
        _mediaFile = File(picked.path);
      });
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);
    try {
      await AuthService().postCaseWithMedia(
        title: _titleController.text.trim(),
        description: _descController.text.trim(),
        imageFile: _imageFile,
        mediaFile: _mediaFile,
      );
      widget.onCaseCreated?.call();
      Navigator.of(context).pop();
    } catch (e) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Failed to create case: $e')));
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Create Case')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(labelText: 'Title'),
                validator: (v) => v == null || v.isEmpty ? 'Enter title' : null,
              ),
              TextFormField(
                controller: _descController,
                decoration: const InputDecoration(labelText: 'Description'),
                maxLines: 3,
                validator:
                    (v) => v == null || v.isEmpty ? 'Enter description' : null,
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  ElevatedButton.icon(
                    onPressed: _pickImage,
                    icon: const Icon(Icons.image),
                    label: const Text('Pick Image'),
                  ),
                  if (_imageFile != null)
                    Padding(
                      padding: const EdgeInsets.only(left: 8.0),
                      child: Text('Image selected'),
                    ),
                ],
              ),
              Row(
                children: [
                  ElevatedButton.icon(
                    onPressed: _pickMedia,
                    icon: const Icon(Icons.attach_file),
                    label: const Text('Pick Media'),
                  ),
                  if (_mediaFile != null)
                    Padding(
                      padding: const EdgeInsets.only(left: 8.0),
                      child: Text('Media selected'),
                    ),
                ],
              ),
              const SizedBox(height: 24),
              _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : ElevatedButton(
                    onPressed: _submit,
                    child: const Text('Create Case'),
                  ),
            ],
          ),
        ),
      ),
    );
  }
}
